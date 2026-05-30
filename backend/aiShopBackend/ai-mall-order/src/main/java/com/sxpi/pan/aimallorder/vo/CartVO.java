package com.sxpi.pan.aimallorder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer checked;
}
