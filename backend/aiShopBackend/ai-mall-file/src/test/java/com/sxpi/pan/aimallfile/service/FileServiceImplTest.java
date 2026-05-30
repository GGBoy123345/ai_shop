package com.sxpi.pan.aimallfile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallfile.config.MinioConfig;
import com.sxpi.pan.aimallfile.entity.FileInfo;
import com.sxpi.pan.aimallfile.mapper.FileInfoMapper;
import com.sxpi.pan.aimallfile.service.impl.FileServiceImpl;
import com.sxpi.pan.aimallfile.vo.FileInfoVO;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileService 单元测试")
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private FileInfoMapper fileInfoMapper;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfig minioConfig;

    private FileInfo testFile;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        when(minioConfig.getBucket()).thenReturn("ai-mall");
        when(minioConfig.getEndpoint()).thenReturn("http://49.87.32.161:21071");

        testFile = new FileInfo();
        testFile.setId(1L);
        testFile.setFileName("abc123.jpg");
        testFile.setOriginalName("商品图.jpg");
        testFile.setUrl("http://49.87.32.161:21071/ai-mall/2026/05/28/abc123.jpg");
        testFile.setSize(102400L);
        testFile.setType("image/jpeg");
        testFile.setExtension("jpg");
        testFile.setBucket("ai-mall");
        testFile.setObjectKey("2026/05/28/abc123.jpg");
        testFile.setUploaderId(userId);
    }

    @Test
    @DisplayName("获取文件列表-成功")
    void getFileList_success() {
        Page<FileInfo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testFile));
        mockPage.setTotal(1);

        when(fileInfoMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<FileInfoVO> result = fileService.getFileList(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("商品图.jpg", result.getRecords().get(0).getOriginalName());
    }

    @Test
    @DisplayName("上传文件-空文件")
    void uploadFile_emptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadFile(userId, emptyFile));
        assertEquals(70001, ex.getCode());
    }

    @Test
    @DisplayName("上传文件-null文件")
    void uploadFile_nullFile() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadFile(userId, null));
        assertEquals(70001, ex.getCode());
    }

    @Test
    @DisplayName("上传文件-不支持的格式")
    void uploadFile_unsupportedFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream", "content".getBytes());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadFile(userId, file));
        assertEquals(70002, ex.getCode());
    }

    @Test
    @DisplayName("上传图片-非图片格式")
    void uploadImage_notImage() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadImage(userId, file));
        assertEquals(70002, ex.getCode());
    }

    @Test
    @DisplayName("批量上传-超出数量限制")
    void uploadBatch_tooManyFiles() {
        MockMultipartFile[] files = new MockMultipartFile[11];
        for (int i = 0; i < 11; i++) {
            files[i] = new MockMultipartFile("file", "test" + i + ".jpg", "image/jpeg", "content".getBytes());
        }

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadBatch(userId, files));
        assertEquals(70006, ex.getCode());
    }

    @Test
    @DisplayName("批量上传-空文件列表")
    void uploadBatch_emptyFiles() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.uploadBatch(userId, new MockMultipartFile[0]));
        assertEquals(70001, ex.getCode());
    }

    @Test
    @DisplayName("获取文件信息-成功")
    void getFileInfo_success() {
        when(fileInfoMapper.selectById(1L)).thenReturn(testFile);

        FileInfoVO result = fileService.getFileInfo(1L);

        assertNotNull(result);
        assertEquals("商品图.jpg", result.getOriginalName());
        assertEquals("jpg", result.getExtension());
    }

    @Test
    @DisplayName("获取文件信息-不存在")
    void getFileInfo_notFound() {
        when(fileInfoMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.getFileInfo(999L));
        assertEquals(70007, ex.getCode());
    }

    @Test
    @DisplayName("删除文件-不存在")
    void deleteFile_notFound() {
        when(fileInfoMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.deleteFile(userId, 999L));
        assertEquals(70007, ex.getCode());
    }

    @Test
    @DisplayName("删除文件-无权操作")
    void deleteFile_notOwner() {
        when(fileInfoMapper.selectById(1L)).thenReturn(testFile);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileService.deleteFile(999L, 1L));
        assertEquals(70008, ex.getCode());
    }

    @Test
    @DisplayName("获取文件信息(内部)-成功")
    void getFileInfoInternal_success() {
        when(fileInfoMapper.selectById(1L)).thenReturn(testFile);

        FileInfoVO result = fileService.getFileInfoInternal(1L);

        assertNotNull(result);
        assertEquals("商品图.jpg", result.getOriginalName());
    }
}
