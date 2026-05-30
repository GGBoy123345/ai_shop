package com.sxpi.pan.aimallproduct.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    private Long categoryId;
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sort;
    private Integer status; // 商品状态：0下架 1上架 2待审核
    private Integer page = 1;
    private Integer size = 10;
}
