package com.sxpi.pan.aimallorder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private Long skuId;
    private Integer quantity = 1;
}
