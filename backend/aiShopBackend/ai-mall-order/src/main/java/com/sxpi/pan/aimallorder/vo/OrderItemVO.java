package com.sxpi.pan.aimallorder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productTitle;
    private String productImage;
    private String skuAttributes;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
}
