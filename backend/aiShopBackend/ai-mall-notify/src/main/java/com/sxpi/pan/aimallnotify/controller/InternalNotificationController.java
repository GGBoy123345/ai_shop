package com.sxpi.pan.aimallnotify.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallnotify.dto.CreateNotificationDTO;
import com.sxpi.pan.aimallnotify.dto.SendEmailDTO;
import com.sxpi.pan.aimallnotify.dto.SendSmsDTO;
import com.sxpi.pan.aimallnotify.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notifications")
    public Result<Void> createNotification(@Valid @RequestBody CreateNotificationDTO dto) {
        notificationService.createNotification(dto);
        return Result.success(null);
    }

    @PostMapping("/sms/send")
    public Result<Void> sendSms(@Valid @RequestBody SendSmsDTO dto) {
        notificationService.sendSms(dto);
        return Result.success(null);
    }

    @PostMapping("/sms/send/batch")
    public Result<Void> sendBatchSms(@Valid @RequestBody java.util.List<SendSmsDTO> list) {
        for (SendSmsDTO dto : list) {
            notificationService.sendSms(dto);
        }
        return Result.success(null);
    }

    @PostMapping("/emails/send")
    public Result<Void> sendEmail(@Valid @RequestBody SendEmailDTO dto) {
        notificationService.sendEmail(dto);
        return Result.success(null);
    }

    @PostMapping("/emails/send/template")
    public Result<Void> sendTemplateEmail(@Valid @RequestBody SendEmailDTO dto) {
        notificationService.sendEmail(dto);
        return Result.success(null);
    }
}
