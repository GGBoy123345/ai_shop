package com.sxpi.pan.aimallorder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("logistics")
public class Logistics extends BaseEntity {
    private Long orderId;
    private String logisticsCompany;
    private String logisticsNo;
    private Integer status;
    private String traces;
}
