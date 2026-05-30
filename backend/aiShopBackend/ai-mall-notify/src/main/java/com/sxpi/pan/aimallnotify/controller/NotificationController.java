package com.sxpi.pan.aimallnotify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallnotify.service.NotificationService;
import com.sxpi.pan.aimallnotify.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Result<Page<NotificationVO>> getNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead) {
        return Result.success(notificationService.getNotifications(userId, page, size, type, isRead));
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        notificationService.markRead(userId, id);
        return Result.success(null);
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllRead(userId);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        notificationService.delete(userId, id);
        return Result.success(null);
    }
}
