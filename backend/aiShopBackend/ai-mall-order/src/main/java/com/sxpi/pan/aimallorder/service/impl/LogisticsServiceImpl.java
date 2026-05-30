package com.sxpi.pan.aimallorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallorder.entity.Logistics;
import com.sxpi.pan.aimallorder.mapper.LogisticsMapper;
import com.sxpi.pan.aimallorder.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {

    private final LogisticsMapper logisticsMapper;

    @Override
    public Logistics getLogisticsByOrderId(Long orderId) {
        return logisticsMapper.selectOne(
                new LambdaQueryWrapper<Logistics>().eq(Logistics::getOrderId, orderId));
    }
}
