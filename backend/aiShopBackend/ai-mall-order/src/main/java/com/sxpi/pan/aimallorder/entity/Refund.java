package com.sxpi.pan.aimallorder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("refund")
public class Refund extends BaseEntity {
    private Long orderId;
    private Long userId;
    private String refundNo;
    private BigDecimal amount;
    private String reason;
    private String description;
    private String images;
    private Integer status;
    private String rejectReason;
}
