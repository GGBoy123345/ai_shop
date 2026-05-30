package com.sxpi.pan.aimalluser.vo;

import lombok.Data;

@Data
public class LoginVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String role;
    private String token;
    private String refreshToken;
}
