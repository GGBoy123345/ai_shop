package com.sxpi.pan.aimallorder.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private String addressSnapshot;
    private String payMethod;
    private String payTime;
    private String shipTime;
    private String receiveTime;
    private String remark;
    private String createTime;
    private List<OrderItemVO> items;
}
