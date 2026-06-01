package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallorder.dto.OrderCreateDTO;
import com.sxpi.pan.aimallorder.dto.OrderQueryDTO;
import com.sxpi.pan.aimallorder.dto.ShipDTO;
import com.sxpi.pan.aimallorder.vo.OrderDetailVO;
import com.sxpi.pan.aimallorder.vo.OrderVO;

import java.util.Map;

public interface OrderService {
    OrderVO createOrder(Long userId, OrderCreateDTO dto);
    Page<OrderVO> getOrderList(Long userId, OrderQueryDTO query);
    OrderDetailVO getOrderDetail(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId);
    void confirmReceive(Long userId, Long orderId);
    void deleteOrder(Long userId, Long orderId);
    void shipOrder(Long orderId, ShipDTO dto);
    Map<String, Object> getOrderStatistics(Long merchantId);
    long countOrders();

    /**
     * 获取近7天订单趋势
     */
    Map<String, Object> getOrderTrend();

    /**
     * 获取近7天销售额趋势
     */
    Map<String, Object> getSalesTrend();

    void payOrder(Long id, Long userId);
}
