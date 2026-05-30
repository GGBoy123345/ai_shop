package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVO {
    private Long id;
    private Long productId;
    private BigDecimal price;
    private Integer stock;
    private String attributes;
    private String image;
}
