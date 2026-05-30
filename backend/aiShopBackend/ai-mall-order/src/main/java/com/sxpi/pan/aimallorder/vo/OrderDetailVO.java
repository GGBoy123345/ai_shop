package com.sxpi.pan.aimallorder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailVO extends OrderVO {
    private String logisticsCompany;
    private String logisticsNo;
    private String logisticsTraces;
}
