package com.sxpi.pan.aimallfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallfile.config.MinioConfig;
import com.sxpi.pan.aimallfile.entity.FileInfo;
import com.sxpi.pan.aimallfile.mapper.FileInfoMapper;
import com.sxpi.pan.aimallfile.service.FileService;
import com.sxpi.pan.aimallfile.vo.FileInfoVO;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoMapper fileInfoMapper;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /** 浏览器缓存 30 天 */
    private static final Map<String, String> CACHE_HEADERS = Map.of("Cache-Control", "max-age=2592000");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx",
            "mp4", "webm", "mov", "avi"
    );
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "webm", "mov", "avi");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    @Override
    public Page<FileInfoVO> getFileList(Integer page, Integer size) {
        Page<FileInfo> pageParam = new Page<>(page, size);
        Page<FileInfo> result = fileInfoMapper.selectPage(pageParam,
                new LambdaQueryWrapper<FileInfo>().orderByDesc(FileInfo::getCreateTime));
        Page<FileInfoVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public FileInfoVO uploadFile(Long userId, MultipartFile file) {
        validateFile(file, MAX_FILE_SIZE);
        return toVO(doUpload(file, userId, null));
    }

    @Override
    public FileInfoVO uploadImage(Long userId, MultipartFile file) {
        validateFile(file, MAX_IMAGE_SIZE);
        String ext = getExtension(file.getOriginalFilename());
        if (!IMAGE_EXTENSIONS.contains(ext)) {
            throw new BusinessException(70002, "文件格式不支持，仅支持jpg/jpeg/png/gif");
        }
        FileInfo info = doUpload(file, userId, null);
        // 生成缩略图
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage != null) {
                int targetWidth = 300;
                int targetHeight = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * targetWidth);
                BufferedImage thumb = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                thumb.getGraphics().drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

                ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
                ImageIO.write(thumb, ext, thumbOs);
                String thumbObjectName = info.getObjectKey().replace("." + ext, "_thumb." + ext);

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(thumbObjectName)
                        .stream(new ByteArrayInputStream(thumbOs.toByteArray()), thumbOs.size(), -1)
                        .contentType(file.getContentType())
                        .headers(CACHE_HEADERS)
                        .build());

                info.setThumbnailUrl(buildFileUrl(thumbObjectName));
                info.setWidth(originalImage.getWidth());
                info.setHeight(originalImage.getHeight());
                fileInfoMapper.updateById(info);
            }
        } catch (Exception e) {
            log.warn("缩略图生成失败: {}", e.getMessage());
        }
        return toVO(info);
    }

    @Override
    public FileInfoVO uploadVideo(Long userId, MultipartFile file) {
        validateFile(file, MAX_VIDEO_SIZE);
        String ext = getExtension(file.getOriginalFilename());
        if (!VIDEO_EXTENSIONS.contains(ext)) {
            throw new BusinessException(70002, "文件格式不支持，仅支持mp4/webm/mov/avi");
        }
        return toVO(doUpload(file, userId, "video"));
    }

    @Override
    public List<FileInfoVO> uploadBatch(Long userId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException(70001, "文件列表为空");
        }
        if (files.length > 10) {
            throw new BusinessException(70006, "文件数量超出限制，最多10个");
        }
        List<FileInfoVO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                validateFile(file, MAX_FILE_SIZE);
                results.add(toVO(doUpload(file, userId, null)));
            } catch (BusinessException e) {
                FileInfoVO failVO = new FileInfoVO();
                failVO.setOriginalName(file.getOriginalFilename());
                failVO.setStatus("fail");
                failVO.setErrorMsg(e.getMessage());
                results.add(failVO);
            }
        }
        return results;
    }

    @Override
    public FileInfoVO getFileInfo(Long id) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException(70007, "文件记录不存在");
        }
        return toVO(info);
    }

    @Override
    public void deleteFile(Long userId, Long id) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException(70007, "文件记录不存在");
        }
        if (!info.getUploaderId().equals(userId)) {
            throw new BusinessException(70008, "无权删除该文件");
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(info.getObjectKey())
                    .build());
            // 删除缩略图
            if (info.getThumbnailUrl() != null) {
                String ext = info.getExtension();
                String thumbObjectName = info.getObjectKey().replace("." + ext, "_thumb." + ext);
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(thumbObjectName)
                        .build());
            }
        } catch (Exception e) {
            log.error("MinIO删除文件失败: {}", e.getMessage());
            throw new BusinessException(70009, "文件删除失败");
        }
        fileInfoMapper.deleteById(id);
    }

    @Override
    public FileInfoVO getFileInfoInternal(Long id) {
        return getFileInfo(id);
    }

    @Override
    public FileInfoVO uploadInternal(MultipartFile file, Long uploaderId, String bizType) {
        validateFile(file, MAX_FILE_SIZE);
        return toVO(doUpload(file, uploaderId, bizType));
    }

    private FileInfo doUpload(MultipartFile file, Long userId, String bizType) {
        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + "." + ext;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectKey = datePath + "/" + fileName;
        String bucket = minioConfig.getBucket();

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .headers(CACHE_HEADERS)
                    .build());
        } catch (Exception e) {
            log.error("MinIO上传失败: {}", e.getMessage());
            throw new BusinessException(70004, "文件上传失败");
        }

        String url = buildFileUrl(objectKey);

        FileInfo info = new FileInfo();
        info.setFileName(fileName);
        info.setOriginalName(originalName);
        info.setUrl(url);
        info.setSize(file.getSize());
        info.setType(file.getContentType());
        info.setExtension(ext);
        info.setBucket(bucket);
        info.setObjectKey(objectKey);
        info.setUploaderId(userId);
        info.setBizType(bizType);

        // 图片读取宽高
        if (IMAGE_EXTENSIONS.contains(ext)) {
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    info.setWidth(image.getWidth());
                    info.setHeight(image.getHeight());
                }
            } catch (IOException ignored) {
            }
        }

        fileInfoMapper.insert(info);
        return info;
    }

    private void validateFile(MultipartFile file, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(70001, "文件为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(70003, "文件大小超出限制");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(70002, "文件格式不支持");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private FileInfoVO toVO(FileInfo info) {
        FileInfoVO vo = new FileInfoVO();
        BeanUtils.copyProperties(info, vo);
        // 用 objectKey 动态生成 URL，避免数据库中存储的 URL 因 endpoint 变更而失效
        if (info.getObjectKey() != null) {
            vo.setUrl(buildFileUrl(info.getObjectKey()));
        }
        if (info.getThumbnailUrl() != null && info.getExtension() != null) {
            String ext = info.getExtension();
            String thumbKey = info.getObjectKey().replace("." + ext, "_thumb." + ext);
            vo.setThumbnailUrl(buildFileUrl(thumbKey));
        }
        return vo;
    }

    /**
     * 根据 objectKey 动态拼接文件访问 URL
     */
    private String buildFileUrl(String objectKey) {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + objectKey;
    }
}
