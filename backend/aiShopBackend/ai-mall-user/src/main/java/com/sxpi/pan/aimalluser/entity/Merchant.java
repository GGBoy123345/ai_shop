package com.sxpi.pan.aimalluser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {
    private Long userId;
    private String shopName;
    private String licenseNo;
    private String contactPhone;
    private String contactName;
    private String description;
    private String logo;
    private Integer status;
    private String auditRemark;
}
