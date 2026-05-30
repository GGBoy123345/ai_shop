package com.sxpi.pan.aimallorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallorder.dto.OrderCreateDTO;
import com.sxpi.pan.aimallorder.dto.OrderQueryDTO;
import com.sxpi.pan.aimallorder.dto.ShipDTO;
import com.sxpi.pan.aimallorder.service.OrderService;
import com.sxpi.pan.aimallorder.vo.OrderDetailVO;
import com.sxpi.pan.aimallorder.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> createOrder(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.createOrder(userId, dto));
    }

    @GetMapping
    public Result<Page<OrderVO>> getOrderList(@RequestHeader("X-User-Id") Long userId,
                                               OrderQueryDTO query) {
        return Result.success(orderService.getOrderList(userId, query));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@RequestHeader("X-User-Id") Long userId,
                                                 @PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirmReceive(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable Long id) {
        orderService.confirmReceive(userId, id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long id) {
        orderService.deleteOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id,
                                   @Valid @RequestBody ShipDTO dto) {
        orderService.shipOrder(id, dto);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics(@RequestHeader("X-User-Id") Long merchantId) {
        return Result.success(orderService.getOrderStatistics(merchantId));
    }

    @GetMapping("/count")
    public Result<Long> countOrders() {
        return Result.success(orderService.countOrders());
    }
}
