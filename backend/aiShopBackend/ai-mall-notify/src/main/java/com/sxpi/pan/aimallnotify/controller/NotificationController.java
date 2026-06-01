package com.sxpi.pan.aimallnotify.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallnotify.entity.SmsLog;
import com.sxpi.pan.aimallnotify.mapper.SmsLogMapper;
import com.sxpi.pan.aimallnotify.service.NotificationService;
import com.sxpi.pan.aimallnotify.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SmsLogMapper smsLogMapper;

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

    @GetMapping("/sms-logs")
    public Result<Page<SmsLog>> getSmsLogs(
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<SmsLog> wrapper = new LambdaQueryWrapper<>();
        if (phone != null && !phone.isEmpty()) {
            wrapper.eq(SmsLog::getPhone, phone);
        }
        wrapper.orderByDesc(SmsLog::getCreateTime);
        return Result.success(smsLogMapper.selectPage(new Page<>(page, size), wrapper));
    }
}
