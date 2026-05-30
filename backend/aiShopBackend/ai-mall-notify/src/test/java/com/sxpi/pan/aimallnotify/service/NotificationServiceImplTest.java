package com.sxpi.pan.aimallnotify.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallnotify.dto.CreateNotificationDTO;
import com.sxpi.pan.aimallnotify.dto.SendEmailDTO;
import com.sxpi.pan.aimallnotify.dto.SendSmsDTO;
import com.sxpi.pan.aimallnotify.entity.Notification;
import com.sxpi.pan.aimallnotify.mapper.NotificationMapper;
import com.sxpi.pan.aimallnotify.service.impl.NotificationServiceImpl;
import com.sxpi.pan.aimallnotify.vo.NotificationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUserId(100L);
        testNotification.setTitle("测试通知");
        testNotification.setContent("测试内容");
        testNotification.setType("system");
        testNotification.setIsRead(0);
        testNotification.setDeleted(0);
        testNotification.setCreateTime(LocalDateTime.now());
    }

    @Test
    void getNotifications_success() {
        Page<Notification> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testNotification));
        page.setTotal(1);
        when(notificationMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<NotificationVO> result = notificationService.getNotifications(100L, 1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("测试通知", result.getRecords().get(0).getTitle());
    }

    @Test
    void getNotifications_withTypeFilter() {
        Page<Notification> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testNotification));
        page.setTotal(1);
        when(notificationMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<NotificationVO> result = notificationService.getNotifications(100L, 1, 10, "system", null);

        assertNotNull(result);
        verify(notificationMapper).selectPage(any(), any());
    }

    @Test
    void getUnreadCount_success() {
        when(notificationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

        Integer count = notificationService.getUnreadCount(100L);

        assertEquals(3, count);
    }

    @Test
    void markRead_success() {
        when(notificationMapper.selectById(1L)).thenReturn(testNotification);
        when(notificationMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        assertDoesNotThrow(() -> notificationService.markRead(100L, 1L));
        verify(notificationMapper).update(isNull(), any());
    }

    @Test
    void markRead_notFound() {
        when(notificationMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> notificationService.markRead(100L, 999L));
    }

    @Test
    void markRead_wrongUser() {
        when(notificationMapper.selectById(1L)).thenReturn(testNotification);

        assertThrows(BusinessException.class, () -> notificationService.markRead(200L, 1L));
    }

    @Test
    void markAllRead_success() {
        when(notificationMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(5);

        assertDoesNotThrow(() -> notificationService.markAllRead(100L));
        verify(notificationMapper).update(isNull(), any());
    }

    @Test
    void delete_success() {
        when(notificationMapper.selectById(1L)).thenReturn(testNotification);
        when(notificationMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> notificationService.delete(100L, 1L));
        verify(notificationMapper).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(notificationMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> notificationService.delete(100L, 999L));
    }

    @Test
    void delete_wrongUser() {
        when(notificationMapper.selectById(1L)).thenReturn(testNotification);

        assertThrows(BusinessException.class, () -> notificationService.delete(200L, 1L));
    }

    @Test
    void createNotification_success() {
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(100L);
        dto.setTitle("新通知");
        dto.setContent("通知内容");
        dto.setType("order");

        assertDoesNotThrow(() -> notificationService.createNotification(dto));
        verify(notificationMapper).insert(any(Notification.class));
    }

    @Test
    void createNotification_defaultType() {
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(100L);
        dto.setTitle("新通知");
        dto.setContent("通知内容");

        assertDoesNotThrow(() -> notificationService.createNotification(dto));
        verify(notificationMapper).insert(argThat(n -> "system".equals(n.getType())));
    }

    @Test
    void sendSms_mockSuccess() {
        SendSmsDTO dto = new SendSmsDTO();
        dto.setPhone("13800000000");
        dto.setContent("验证码1234");

        assertDoesNotThrow(() -> notificationService.sendSms(dto));
    }

    @Test
    void sendEmail_mockSuccess() {
        SendEmailDTO dto = new SendEmailDTO();
        dto.setTo("test@example.com");
        dto.setSubject("测试邮件");
        dto.setContent("邮件内容");

        assertDoesNotThrow(() -> notificationService.sendEmail(dto));
    }
}
