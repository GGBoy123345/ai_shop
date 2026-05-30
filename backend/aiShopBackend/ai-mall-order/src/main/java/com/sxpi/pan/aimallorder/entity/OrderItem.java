package com.sxpi.pan.aimallorder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
public class OrderItem extends BaseEntity {
    private Long orderId;
    private Long productId;
    private Long skuId;
    private String productTitle;
    private String productImage;
    private String skuAttributes;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
}
