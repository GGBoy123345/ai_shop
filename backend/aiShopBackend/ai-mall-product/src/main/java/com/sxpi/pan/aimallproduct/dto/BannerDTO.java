package com.sxpi.pan.aimallproduct.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BannerDTO {
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
    private String linkUrl;
    private Integer sort;
}
