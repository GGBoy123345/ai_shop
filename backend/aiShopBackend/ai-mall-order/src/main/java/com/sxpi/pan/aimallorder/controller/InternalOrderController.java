package com.sxpi.pan.aimallorder.controller;

import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallorder.entity.Order;
import com.sxpi.pan.aimallorder.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderMapper orderMapper;

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id,
                                       @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(40421, "订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/orderNo/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo,
                                            @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Order order = orderMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(40421, "订单不存在");
        }
        return Result.success(order);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id,
                                            @RequestParam Integer status,
                                            @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(40421, "订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
        return Result.success();
    }

    private void validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "内部调用令牌缺失");
        }
    }
}
