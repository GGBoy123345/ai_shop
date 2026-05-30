package com.sxpi.pan.aimallorder.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
