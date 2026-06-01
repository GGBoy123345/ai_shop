package com.sxpi.pan.aimallproduct.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotBlank(message = "商品标题不能为空")
    private String title;
    private String subtitle;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private BigDecimal weight;
    private String mainImage;
    private String images;
    private String video;
    private String description;
    private List<ProductAttributeDTO> attributes;

    @Data
    public static class ProductAttributeDTO {
        private Long templateId;
        private String value;
    }
}
