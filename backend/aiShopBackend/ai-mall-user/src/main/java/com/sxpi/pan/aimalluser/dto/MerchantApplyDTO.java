package com.sxpi.pan.aimalluser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantApplyDTO {
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "营业执照编号不能为空")
    private String licenseNo;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactName;
    private String description;
    private String logo;
}
