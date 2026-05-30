package com.sxpi.pan.aimallsearch.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchResultVO {
    private Long total;
    private Integer page;
    private Integer size;
    private String keyword;
    private List<SearchItemVO> list;

    @Data
    public static class SearchItemVO {
        private Long id;
        private String name;
        private String subtitle;
        private String mainImage;
        private BigDecimal price;
        private BigDecimal marketPrice;
        private Integer sales;
        private Long categoryId;
    }
}
