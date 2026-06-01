package com.sxpi.pan.aimallnotify.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_log")
public class SmsLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String phone;
    private String templateCode;
    private String params;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
