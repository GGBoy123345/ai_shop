package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attribute_template")
public class AttributeTemplate extends BaseEntity {
    private Long categoryId;
    private String name;
    private String inputType;
    private Integer required;
    private Integer sort;
}
