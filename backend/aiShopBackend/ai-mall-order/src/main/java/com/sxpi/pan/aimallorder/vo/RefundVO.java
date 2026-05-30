package com.sxpi.pan.aimallorder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundVO {
    private Long id;
    private String refundNo;
    private Long orderId;
    private BigDecimal amount;
    private String reason;
    private String description;
    private Integer status;
    private String rejectReason;
    private String createTime;
}
