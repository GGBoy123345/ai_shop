package com.sxpi.pan.aimallfile.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallfile.service.FileService;
import com.sxpi.pan.aimallfile.vo.FileInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/internal/files")
@RequiredArgsConstructor
public class InternalFileController {

    private final FileService fileService;

    @GetMapping("/{id}")
    public Result<FileInfoVO> getFileInfo(@PathVariable Long id) {
        return Result.success(fileService.getFileInfoInternal(id));
    }

    @PostMapping("/upload")
    public Result<FileInfoVO> upload(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "uploaderId", required = false) Long uploaderId,
                                     @RequestParam(value = "bizType", required = false) String bizType) {
        return Result.success(fileService.uploadInternal(file, uploaderId, bizType));
    }
}
