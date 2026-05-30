package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailVO extends ProductVO {
    private List<AttributeVO> attributes;
    private List<SkuVO> skus;

    @Data
    public static class AttributeVO {
        private Long templateId;
        private String templateName;
        private String value;
    }
}
