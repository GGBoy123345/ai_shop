package com.sxpi.pan.aimallsearch.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品搜索索引（只读，映射product表）
 */
@Data
@TableName("product")
public class ProductIndex {
    @TableId
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String title;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Integer deleted;
}
