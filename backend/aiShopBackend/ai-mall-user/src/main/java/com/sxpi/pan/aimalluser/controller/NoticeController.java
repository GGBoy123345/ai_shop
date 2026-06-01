package com.sxpi.pan.aimalluser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.entity.Notice;
import com.sxpi.pan.aimalluser.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<Page<Notice>> getActiveNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(noticeService.getActiveNoticeList(page, size));
    }
}
