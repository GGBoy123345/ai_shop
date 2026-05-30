package com.sxpi.pan.aimallfile.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallfile.service.FileService;
import com.sxpi.pan.aimallfile.vo.FileInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public Result<Page<FileInfoVO>> getFileList(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(fileService.getFileList(page, size));
    }

    @PostMapping("/upload")
    public Result<FileInfoVO> upload(@RequestHeader("X-User-Id") Long userId,
                                     @RequestParam("file") MultipartFile file) {
        return Result.success(fileService.uploadFile(userId, file));
    }

    @PostMapping("/upload/image")
    public Result<FileInfoVO> uploadImage(@RequestHeader("X-User-Id") Long userId,
                                          @RequestParam("file") MultipartFile file) {
        return Result.success(fileService.uploadImage(userId, file));
    }

    @PostMapping("/upload/batch")
    public Result<List<FileInfoVO>> uploadBatch(@RequestHeader("X-User-Id") Long userId,
                                                @RequestParam("files") MultipartFile[] files) {
        return Result.success(fileService.uploadBatch(userId, files));
    }

    @GetMapping("/{id}")
    public Result<FileInfoVO> getFileInfo(@PathVariable Long id) {
        return Result.success(fileService.getFileInfo(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long id) {
        fileService.deleteFile(userId, id);
        return Result.success(null);
    }
}
