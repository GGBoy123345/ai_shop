package com.sxpi.pan.aimallfile.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallfile.vo.FileInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    Page<FileInfoVO> getFileList(Integer page, Integer size);
    FileInfoVO uploadFile(Long userId, MultipartFile file);
    FileInfoVO uploadImage(Long userId, MultipartFile file);
    FileInfoVO uploadVideo(Long userId, MultipartFile file);
    List<FileInfoVO> uploadBatch(Long userId, MultipartFile[] files);
    FileInfoVO getFileInfo(Long id);
    void deleteFile(Long userId, Long id);
    FileInfoVO getFileInfoInternal(Long id);
    FileInfoVO uploadInternal(MultipartFile file, Long uploaderId, String bizType);
}
