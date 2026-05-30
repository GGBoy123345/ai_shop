package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallorder.dto.RefundDTO;
import com.sxpi.pan.aimallorder.entity.Order;
import com.sxpi.pan.aimallorder.entity.Refund;
import com.sxpi.pan.aimallorder.mapper.OrderMapper;
import com.sxpi.pan.aimallorder.mapper.RefundMapper;
import com.sxpi.pan.aimallorder.service.impl.RefundServiceImpl;
import com.sxpi.pan.aimallorder.vo.RefundVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefundService 单元测试")
class RefundServiceImplTest {

    @InjectMocks
    private RefundServiceImpl refundService;

    @Mock
    private RefundMapper refundMapper;
    @Mock
    private OrderMapper orderMapper;

    private Order testOrder;
    private Refund testRefund;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(userId);
        testOrder.setStatus(2);

        testRefund = new Refund();
        testRefund.setId(1L);
        testRefund.setOrderId(1L);
        testRefund.setUserId(userId);
        testRefund.setRefundNo("R20260528120000123456");
        testRefund.setAmount(new BigDecimal("99.00"));
        testRefund.setReason("不想要了");
        testRefund.setStatus(0);
    }

    @Test
    @DisplayName("申请退款-成功")
    void applyRefund_success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(refundMapper.insert(any(Refund.class))).thenReturn(1);

        RefundDTO dto = new RefundDTO();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("99.00"));
        dto.setReason("不想要了");

        assertDoesNotThrow(() -> refundService.applyRefund(userId, dto));
        verify(refundMapper).insert(any(Refund.class));
    }

    @Test
    @DisplayName("申请退款-订单不存在")
    void applyRefund_orderNotFound() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        RefundDTO dto = new RefundDTO();
        dto.setOrderId(999L);
        dto.setAmount(new BigDecimal("99.00"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.applyRefund(userId, dto));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("申请退款-无权操作")
    void applyRefund_wrongUser() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        RefundDTO dto = new RefundDTO();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("99.00"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.applyRefund(999L, dto));
        assertEquals(40421, ex.getCode());
    }

    @Test
    @DisplayName("申请退款-待付款状态不可退款")
    void applyRefund_pendingPay() {
        testOrder.setStatus(0);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        RefundDTO dto = new RefundDTO();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("99.00"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.applyRefund(userId, dto));
        assertEquals(40045, ex.getCode());
    }

    @Test
    @DisplayName("申请退款-已取消状态不可退款")
    void applyRefund_cancelled() {
        testOrder.setStatus(4);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        RefundDTO dto = new RefundDTO();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("99.00"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.applyRefund(userId, dto));
        assertEquals(40045, ex.getCode());
    }

    @Test
    @DisplayName("获取退款列表-成功")
    void getRefundList_success() {
        Page<Refund> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testRefund));
        mockPage.setTotal(1);

        when(refundMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<RefundVO> result = refundService.getRefundList(userId, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("R20260528120000123456", result.getRecords().get(0).getRefundNo());
    }

    @Test
    @DisplayName("获取退款详情-成功")
    void getRefundDetail_success() {
        when(refundMapper.selectById(1L)).thenReturn(testRefund);

        RefundVO detail = refundService.getRefundDetail(userId, 1L);

        assertNotNull(detail);
        assertEquals("R20260528120000123456", detail.getRefundNo());
        assertEquals(new BigDecimal("99.00"), detail.getAmount());
    }

    @Test
    @DisplayName("获取退款详情-不存在")
    void getRefundDetail_notFound() {
        when(refundMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.getRefundDetail(userId, 999L));
        assertEquals(40422, ex.getCode());
    }

    @Test
    @DisplayName("获取退款详情-无权操作")
    void getRefundDetail_wrongUser() {
        when(refundMapper.selectById(1L)).thenReturn(testRefund);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.getRefundDetail(999L, 1L));
        assertEquals(40422, ex.getCode());
    }

    @Test
    @DisplayName("审批退款-同意")
    void approveRefund_success() {
        when(refundMapper.selectById(1L)).thenReturn(testRefund);
        when(refundMapper.updateById(any(Refund.class))).thenReturn(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        assertDoesNotThrow(() -> refundService.approveRefund(1L, "同意退款"));
        assertEquals(1, testRefund.getStatus());
        assertEquals(5, testOrder.getStatus());
    }

    @Test
    @DisplayName("审批退款-不存在")
    void approveRefund_notFound() {
        when(refundMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.approveRefund(999L, "同意"));
        assertEquals(40422, ex.getCode());
    }

    @Test
    @DisplayName("审批退款-非待审核状态")
    void approveRefund_wrongStatus() {
        testRefund.setStatus(1);
        when(refundMapper.selectById(1L)).thenReturn(testRefund);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.approveRefund(1L, "同意"));
        assertEquals(40046, ex.getCode());
    }

    @Test
    @DisplayName("拒绝退款-成功")
    void rejectRefund_success() {
        when(refundMapper.selectById(1L)).thenReturn(testRefund);
        when(refundMapper.updateById(any(Refund.class))).thenReturn(1);

        assertDoesNotThrow(() -> refundService.rejectRefund(1L, "不符合退款条件"));
        assertEquals(2, testRefund.getStatus());
        assertEquals("不符合退款条件", testRefund.getRejectReason());
    }

    @Test
    @DisplayName("拒绝退款-不存在")
    void rejectRefund_notFound() {
        when(refundMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.rejectRefund(999L, "拒绝"));
        assertEquals(40422, ex.getCode());
    }

    @Test
    @DisplayName("拒绝退款-非待审核状态")
    void rejectRefund_wrongStatus() {
        testRefund.setStatus(2);
        when(refundMapper.selectById(1L)).thenReturn(testRefund);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> refundService.rejectRefund(1L, "拒绝"));
        assertEquals(40046, ex.getCode());
    }
}
