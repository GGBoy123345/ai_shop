package com.sxpi.pan.aimallorder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @NotNull(message = "退款金额不能为空")
    private BigDecimal amount;
    private String reason;
    private String description;
    private String images;
}
