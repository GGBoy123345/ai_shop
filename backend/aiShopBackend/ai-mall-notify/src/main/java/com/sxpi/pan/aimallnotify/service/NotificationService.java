package com.sxpi.pan.aimallnotify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallnotify.dto.CreateNotificationDTO;
import com.sxpi.pan.aimallnotify.dto.SendEmailDTO;
import com.sxpi.pan.aimallnotify.dto.SendSmsDTO;
import com.sxpi.pan.aimallnotify.vo.NotificationVO;

public interface NotificationService {

    Page<NotificationVO> getNotifications(Long userId, Integer page, Integer size, String type, Boolean isRead);

    Integer getUnreadCount(Long userId);

    void markRead(Long userId, Long id);

    void markAllRead(Long userId);

    void delete(Long userId, Long id);

    void createNotification(CreateNotificationDTO dto);

    void sendSms(SendSmsDTO dto);

    void sendEmail(SendEmailDTO dto);
}
