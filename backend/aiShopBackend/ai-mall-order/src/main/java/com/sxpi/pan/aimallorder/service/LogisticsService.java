package com.sxpi.pan.aimallorder.service;

import com.sxpi.pan.aimallorder.entity.Logistics;

public interface LogisticsService {
    Logistics getLogisticsByOrderId(Long orderId);
}
