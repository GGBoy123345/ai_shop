package com.sxpi.pan.aimallproduct.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttributeTemplateVO {
    private Long id;
    private Long categoryId;
    private String name;
    private String inputType;
    private Integer required;
    private Integer sort;
    private List<OptionVO> options;

    @Data
    public static class OptionVO {
        private Long id;
        private String value;
        private Integer sort;
    }
}
