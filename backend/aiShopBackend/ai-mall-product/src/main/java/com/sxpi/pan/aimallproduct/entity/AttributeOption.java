package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attribute_option")
public class AttributeOption extends BaseEntity {
    private Long templateId;
    private String value;
    private Integer sort;
}
