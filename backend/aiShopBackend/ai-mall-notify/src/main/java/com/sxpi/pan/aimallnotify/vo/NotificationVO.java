package com.sxpi.pan.aimallnotify.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Boolean isRead;
    private LocalDateTime createTime;
}
