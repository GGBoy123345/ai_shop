package com.sxpi.pan.aimallnotify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallnotify.dto.CreateNotificationDTO;
import com.sxpi.pan.aimallnotify.dto.SendEmailDTO;
import com.sxpi.pan.aimallnotify.dto.SendSmsDTO;
import com.sxpi.pan.aimallnotify.entity.Notification;
import com.sxpi.pan.aimallnotify.mapper.NotificationMapper;
import com.sxpi.pan.aimallnotify.service.NotificationService;
import com.sxpi.pan.aimallnotify.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    private static final int ERROR_BASE = 80000;

    @Override
    public Page<NotificationVO> getNotifications(Long userId, Integer page, Integer size, String type, Boolean isRead) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getDeleted, 0)
                .eq(StringUtils.hasText(type), Notification::getType, type)
                .eq(isRead != null, Notification::getIsRead, Boolean.TRUE.equals(isRead) ? 1 : 0)
                .orderByDesc(Notification::getCreateTime);

        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        Page<NotificationVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<NotificationVO> voList = result.getRecords().stream().map(this::toVO).toList();
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .eq(Notification::getDeleted, 0);
        return Math.toIntExact(notificationMapper.selectCount(wrapper));
    }

    @Override
    public void markRead(Long userId, Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException(ERROR_BASE + 1, "通知不存在");
        }
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, id)
                .set(Notification::getIsRead, 1);
        notificationMapper.update(null, wrapper);
    }

    @Override
    public void markAllRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        notificationMapper.update(null, wrapper);
    }

    @Override
    public void delete(Long userId, Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException(ERROR_BASE + 1, "通知不存在");
        }
        notificationMapper.deleteById(id);
    }

    @Override
    public void createNotification(CreateNotificationDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setType(StringUtils.hasText(dto.getType()) ? dto.getType() : "system");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        log.info("通知已创建: userId={}, title={}", dto.getUserId(), dto.getTitle());
    }

    @Override
    public void sendSms(SendSmsDTO dto) {
        // Mock SMS sending - log only
        log.info("[MOCK] 短信发送: phone={}, content={}", dto.getPhone(), dto.getContent());
    }

    @Override
    public void sendEmail(SendEmailDTO dto) {
        // Mock Email sending - log only
        log.info("[MOCK] 邮件发送: to={}, subject={}, content={}", dto.getTo(), dto.getSubject(), dto.getContent());
    }

    private NotificationVO toVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);
        vo.setIsRead(notification.getIsRead() == 1);
        return vo;
    }
}
