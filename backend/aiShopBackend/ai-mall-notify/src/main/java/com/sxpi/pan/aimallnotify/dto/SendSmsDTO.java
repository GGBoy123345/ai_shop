package com.sxpi.pan.aimallnotify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendSmsDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "短信内容不能为空")
    private String content;
    private String templateCode;
}
