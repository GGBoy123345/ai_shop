package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
    private List<CategoryVO> children;
}
