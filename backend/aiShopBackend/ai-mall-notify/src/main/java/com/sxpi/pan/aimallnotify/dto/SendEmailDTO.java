package com.sxpi.pan.aimallnotify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailDTO {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String to;
    @NotBlank(message = "邮件主题不能为空")
    private String subject;
    @NotBlank(message = "邮件内容不能为空")
    private String content;
}
