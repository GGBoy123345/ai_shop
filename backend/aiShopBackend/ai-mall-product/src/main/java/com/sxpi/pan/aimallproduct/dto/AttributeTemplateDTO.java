package com.sxpi.pan.aimallproduct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttributeTemplateDTO {
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotBlank(message = "属性名称不能为空")
    private String name;
    private String inputType;
    private Integer required;
    private Integer sort;
}
