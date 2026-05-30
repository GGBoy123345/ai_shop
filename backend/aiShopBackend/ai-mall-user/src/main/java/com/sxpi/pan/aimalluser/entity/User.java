package com.sxpi.pan.aimalluser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String role;
    private Integer status;
}
