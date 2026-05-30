package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private String images;
    private String description;
    private Integer status;
    private String createTime;
}
