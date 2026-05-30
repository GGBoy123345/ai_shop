package com.sxpi.pan.aimalluser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config")
public class Config extends BaseEntity {
    private String configKey;
    private String configValue;
    private String configDesc;
    private String configGroup;
}
