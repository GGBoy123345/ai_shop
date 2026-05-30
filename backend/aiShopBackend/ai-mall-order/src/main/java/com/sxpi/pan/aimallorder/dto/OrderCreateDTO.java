package com.sxpi.pan.aimallorder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    private String remark;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        private Long skuId;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
