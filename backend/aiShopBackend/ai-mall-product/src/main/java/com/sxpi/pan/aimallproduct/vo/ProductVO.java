package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String title;
    private String subtitle;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer sales;
    private Integer views;
    private String mainImage;
    private String images;
    private String video;
    private String description;
    private BigDecimal weight;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
    private Integer sortOrder;
    private String createTime;
}
