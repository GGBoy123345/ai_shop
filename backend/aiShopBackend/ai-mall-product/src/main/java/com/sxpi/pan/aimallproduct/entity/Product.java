package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {
    private Long merchantId;
    private Long categoryId;
    private String title;
    private String subtitle;
    private BigDecimal price;
    @TableField("market_price")
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
    private String auditRemark;
}
