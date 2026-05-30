package com.sxpi.pan.aimallorder.service.impl;

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
import com.sxpi.pan.aimallorder.service.OrderService;
import com.sxpi.pan.aimallorder.vo.OrderDetailVO;
import com.sxpi.pan.aimallorder.vo.OrderItemVO;
import com.sxpi.pan.aimallorder.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final LogisticsMapper logisticsMapper;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(40040, "订单商品不能为空");
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 计算总金额（简化处理，实际应查询商品价格）
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            // 简化：总价先设为0，实际应查询SKU价格
            totalAmount = totalAmount.add(BigDecimal.ZERO);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(1L); // 简化：默认商家
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(0); // 待付款
        order.setAddressSnapshot("{\"addressId\":" + dto.getAddressId() + "}");
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        // 创建订单项
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setProductTitle("商品#" + item.getProductId());
            orderItem.setPrice(BigDecimal.ZERO);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(BigDecimal.ZERO);
            orderItemMapper.insert(orderItem);
        }

        return toOrderVO(order);
    }

    @Override
    public Page<OrderVO> getOrderList(Long userId, OrderQueryDTO query) {
        Page<Order> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = orderMapper.selectPage(page, wrapper);
        Page<OrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toOrderVO).toList());
        return voPage;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40421, "订单不存在");
        }

        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);

        // 查询订单项
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).toList());

        // 查询物流
        Logistics logistics = logisticsMapper.selectOne(
                new LambdaQueryWrapper<Logistics>().eq(Logistics::getOrderId, orderId));
        if (logistics != null) {
            vo.setLogisticsCompany(logistics.getLogisticsCompany());
            vo.setLogisticsNo(logistics.getLogisticsNo());
            vo.setLogisticsTraces(logistics.getTraces());
        }

        return vo;
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40421, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(40041, "只有待付款的订单可以取消");
        }
        order.setStatus(4); // 已取消
        orderMapper.updateById(order);
    }

    @Override
    public void confirmReceive(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40421, "订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(40042, "只有待收货的订单可以确认收货");
        }
        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void deleteOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40421, "订单不存在");
        }
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            throw new BusinessException(40043, "只有已完成或已取消的订单可以删除");
        }
        orderMapper.deleteById(orderId);
    }

    @Override
    public void shipOrder(Long orderId, ShipDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(40421, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(40044, "只有待发货的订单可以发货");
        }
        order.setStatus(2); // 待收货
        order.setShipTime(LocalDateTime.now());
        order.setLogisticsCompany(dto.getLogisticsCompany());
        order.setLogisticsNo(dto.getLogisticsNo());
        orderMapper.updateById(order);

        // 创建物流记录
        Logistics logistics = new Logistics();
        logistics.setOrderId(orderId);
        logistics.setLogisticsCompany(dto.getLogisticsCompany());
        logistics.setLogisticsNo(dto.getLogisticsNo());
        logistics.setStatus(0);
        logisticsMapper.insert(logistics);
    }

    @Override
    public Map<String, Object> getOrderStatistics(Long merchantId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId)));
        stats.put("pendingPay", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId).eq(Order::getStatus, 0)));
        stats.put("pendingShip", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId).eq(Order::getStatus, 1)));
        stats.put("shipped", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId).eq(Order::getStatus, 2)));
        stats.put("completed", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId).eq(Order::getStatus, 3)));
        return stats;
    }

    @Override
    public long countOrders() {
        return orderMapper.selectCount(null);
    }

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        // 查询订单项
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).toList());
        return vo;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return timestamp + random;
    }
}
