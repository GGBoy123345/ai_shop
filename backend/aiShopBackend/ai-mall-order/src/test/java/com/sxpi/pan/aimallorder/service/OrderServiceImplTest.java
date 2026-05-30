package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallorder.dto.OrderCreateDTO;
import com.sxpi.pan.aimallorder.dto.OrderQueryDTO;
import com.sxpi.pan.aimallorder.dto.ShipDTO;
import com.sxpi.pan.aimallorder.entity.Order;
import com.sxpi.pan.aimallorder.entity.OrderItem;
import com.sxpi.pan.aimallorder.entity.Logistics;
import com.sxpi.pan.aimallorder.mapper.OrderMapper;
import com.sxpi.pan.aimallorder.mapper.OrderItemMapper;
import com.sxpi.pan.aimallorder.mapper.LogisticsMapper;
import com.sxpi.pan.aimallorder.service.impl.OrderServiceImpl;
import com.sxpi.pan.aimallorder.vo.OrderDetailVO;
import com.sxpi.pan.aimallorder.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 单元测试")
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private LogisticsMapper logisticsMapper;

    private Order testOrder;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("20260528120000123456");
        testOrder.setUserId(userId);
        testOrder.setMerchantId(10L);
        testOrder.setTotalAmount(new BigDecimal("199.00"));
        testOrder.setPayAmount(new BigDecimal("199.00"));
        testOrder.setStatus(0);
        testOrder.setAddressSnapshot("{\"addressId\":1}");
        testOrder.setRemark("测试订单");
    }

    @Test
    @DisplayName("创建订单-成功")
    void createOrder_success() {
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setAddressId(1L);
        dto.setRemark("测试");

        OrderCreateDTO.OrderItemDTO item = new OrderCreateDTO.OrderItemDTO();
        item.setProductId(100L);
        item.setSkuId(200L);
        item.setQuantity(2);
        dto.setItems(Collections.singletonList(item));

        OrderVO result = orderService.createOrder(userId, dto);

        assertNotNull(result);
        assertEquals(0, result.getStatus());
        verify(orderMapper).insert(any(Order.class));
        verify(orderItemMapper).insert(any(OrderItem.class));
    }

    @Test
    @DisplayName("创建订单-商品为空")
    void createOrder_emptyItems() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setAddressId(1L);
        dto.setItems(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(userId, dto));
        assertEquals(40040, ex.getCode());
    }

    @Test
    @DisplayName("创建订单-商品列表为null")
    void createOrder_nullItems() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setAddressId(1L);
        dto.setItems(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(userId, dto));
        assertEquals(40040, ex.getCode());
    }

    @Test
    @DisplayName("获取订单列表-成功")
    void getOrderList_success() {
        Page<Order> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testOrder));
        mockPage.setTotal(1);

        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        OrderQueryDTO query = new OrderQueryDTO();
        Page<OrderVO> result = orderService.getOrderList(userId, query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("获取订单详情-成功")
    void getOrderDetail_success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(logisticsMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        OrderDetailVO detail = orderService.getOrderDetail(userId, 1L);

        assertNotNull(detail);
        assertEquals("20260528120000123456", detail.getOrderNo());
    }

    @Test
    @DisplayName("获取订单详情-不存在")
    void getOrderDetail_notFound() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.getOrderDetail(userId, 999L));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("获取订单详情-无权操作")
    void getOrderDetail_wrongUser() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.getOrderDetail(999L, 1L));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("取消订单-成功")
    void cancelOrder_success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        assertDoesNotThrow(() -> orderService.cancelOrder(userId, 1L));
        assertEquals(4, testOrder.getStatus());
    }

    @Test
    @DisplayName("取消订单-不存在")
    void cancelOrder_notFound() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancelOrder(userId, 999L));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("取消订单-非待付款状态")
    void cancelOrder_wrongStatus() {
        testOrder.setStatus(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancelOrder(userId, 1L));
        assertEquals(40041, ex.getCode());
    }

    @Test
    @DisplayName("确认收货-成功")
    void confirmReceive_success() {
        testOrder.setStatus(2);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        assertDoesNotThrow(() -> orderService.confirmReceive(userId, 1L));
        assertEquals(3, testOrder.getStatus());
        assertNotNull(testOrder.getReceiveTime());
    }

    @Test
    @DisplayName("确认收货-不存在")
    void confirmReceive_notFound() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.confirmReceive(userId, 999L));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("确认收货-非待收货状态")
    void confirmReceive_wrongStatus() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.confirmReceive(userId, 1L));
        assertEquals(40042, ex.getCode());
    }

    @Test
    @DisplayName("删除订单-成功(已完成)")
    void deleteOrder_completed() {
        testOrder.setStatus(3);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> orderService.deleteOrder(userId, 1L));
        verify(orderMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除订单-成功(已取消)")
    void deleteOrder_cancelled() {
        testOrder.setStatus(4);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> orderService.deleteOrder(userId, 1L));
        verify(orderMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除订单-状态不允许")
    void deleteOrder_wrongStatus() {
        testOrder.setStatus(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.deleteOrder(userId, 1L));
        assertEquals(40043, ex.getCode());
    }

    @Test
    @DisplayName("发货-成功")
    void shipOrder_success() {
        testOrder.setStatus(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(logisticsMapper.insert(any(Logistics.class))).thenReturn(1);

        ShipDTO dto = new ShipDTO();
        dto.setLogisticsCompany("顺丰速运");
        dto.setLogisticsNo("SF1234567890");

        assertDoesNotThrow(() -> orderService.shipOrder(1L, dto));
        assertEquals(2, testOrder.getStatus());
        assertNotNull(testOrder.getShipTime());
        verify(logisticsMapper).insert(any(Logistics.class));
    }

    @Test
    @DisplayName("发货-订单不存在")
    void shipOrder_notFound() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        ShipDTO dto = new ShipDTO();
        dto.setLogisticsCompany("顺丰速运");
        dto.setLogisticsNo("SF1234567890");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.shipOrder(999L, dto));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("发货-非待发货状态")
    void shipOrder_wrongStatus() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        ShipDTO dto = new ShipDTO();
        dto.setLogisticsCompany("顺丰速运");
        dto.setLogisticsNo("SF1234567890");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.shipOrder(1L, dto));
        assertEquals(40044, ex.getCode());
    }

    @Test
    @DisplayName("获取订单统计-成功")
    void getOrderStatistics_success() {
        when(orderMapper.selectCount(any(LambdaQueryWrapper.class)))
                .thenReturn(10L)
                .thenReturn(3L)
                .thenReturn(2L)
                .thenReturn(4L)
                .thenReturn(1L);

        Map<String, Object> stats = orderService.getOrderStatistics(10L);

        assertNotNull(stats);
        assertEquals(10L, stats.get("total"));
        assertEquals(3L, stats.get("pendingPay"));
        assertEquals(2L, stats.get("pendingShip"));
        assertEquals(4L, stats.get("shipped"));
        assertEquals(1L, stats.get("completed"));
    }
}
