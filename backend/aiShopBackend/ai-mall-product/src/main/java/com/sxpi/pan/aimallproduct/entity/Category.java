package com.sxpi.pan.aimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {
    private String name;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
}
