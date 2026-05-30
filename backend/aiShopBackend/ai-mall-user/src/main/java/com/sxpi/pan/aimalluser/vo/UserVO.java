package com.sxpi.pan.aimalluser.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String role;
    private Integer status;
    private String createTime;
}
