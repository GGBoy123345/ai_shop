package com.sxpi.pan.aimallproduct.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttributeOptionDTO {
    @NotBlank(message = "选项值不能为空")
    private String value;
    private Integer sort;
}
