package com.sxpi.pan.aimallnotify.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class Notification extends BaseEntity {
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
}
