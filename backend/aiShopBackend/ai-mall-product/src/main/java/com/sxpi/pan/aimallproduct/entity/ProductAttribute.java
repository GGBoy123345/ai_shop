package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_attribute")
public class ProductAttribute extends BaseEntity {
    private Long productId;
    private Long templateId;
    private String value;
}
