package com.sxpi.pan.aimalluser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String module;
    private String operation;
    private String method;
    private String url;
    private Long operatorId;
    private String operatorName;
    private String ip;
    private String params;
    private Integer result;
    private String errorMsg;
    private LocalDateTime createTime;
}
