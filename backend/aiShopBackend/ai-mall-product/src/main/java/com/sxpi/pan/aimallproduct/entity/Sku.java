package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sku")
public class Sku extends BaseEntity {
    private Long productId;
    private BigDecimal price;
    private Integer stock;
    private String attributes;
    private String image;
}
