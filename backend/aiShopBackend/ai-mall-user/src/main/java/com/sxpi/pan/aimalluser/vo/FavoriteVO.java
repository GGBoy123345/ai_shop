package com.sxpi.pan.aimalluser.vo;

import lombok.Data;

@Data
public class FavoriteVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private java.math.BigDecimal productPrice;
    private String createTime;
}
