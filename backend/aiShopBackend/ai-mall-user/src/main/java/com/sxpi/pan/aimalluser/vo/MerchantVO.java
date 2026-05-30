package com.sxpi.pan.aimalluser.vo;

import lombok.Data;

@Data
public class MerchantVO {
    private Long id;
    private Long userId;
    private String shopName;
    private String licenseNo;
    private String contactPhone;
    private String contactName;
    private String description;
    private String logo;
    private Integer status;
    private String auditRemark;
    private String createTime;
}
