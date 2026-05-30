package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallorder.entity.Logistics;
import com.sxpi.pan.aimallorder.mapper.LogisticsMapper;
import com.sxpi.pan.aimallorder.service.impl.LogisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsServiceImplTest {

    @Mock
    private LogisticsMapper logisticsMapper;

    @InjectMocks
    private LogisticsServiceImpl logisticsService;

    @Test
    void getLogisticsByOrderId_found() {
        Logistics logistics = new Logistics();
        logistics.setId(1L);
        logistics.setOrderId(100L);
        logistics.setLogisticsCompany("顺丰速运");
        logistics.setLogisticsNo("SF1234567890");
        logistics.setStatus(2);
        logistics.setTraces("[{\"time\":\"2024-01-01 10:00\",\"info\":\"已揽收\"}]");
        when(logisticsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(logistics);

        Logistics result = logisticsService.getLogisticsByOrderId(100L);

        assertNotNull(result);
        assertEquals("顺丰速运", result.getLogisticsCompany());
        assertEquals("SF1234567890", result.getLogisticsNo());
        assertEquals(2, result.getStatus());
    }

    @Test
    void getLogisticsByOrderId_notFound() {
        when(logisticsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        Logistics result = logisticsService.getLogisticsByOrderId(999L);

        assertNull(result);
    }
}
