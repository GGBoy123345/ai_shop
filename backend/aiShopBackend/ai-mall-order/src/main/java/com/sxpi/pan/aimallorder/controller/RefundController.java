package com.sxpi.pan.aimallorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallorder.dto.RefundDTO;
import com.sxpi.pan.aimallorder.service.RefundService;
import com.sxpi.pan.aimallorder.vo.RefundVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public Result<Void> applyRefund(@RequestHeader("X-User-Id") Long userId,
                                     @Valid @RequestBody RefundDTO dto) {
        refundService.applyRefund(userId, dto);
        return Result.success();
    }

    @GetMapping
    public Result<Page<RefundVO>> getRefundList(@RequestHeader("X-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(refundService.getRefundList(userId, page, size));
    }

    @GetMapping("/{id}")
    public Result<RefundVO> getRefundDetail(@RequestHeader("X-User-Id") Long userId,
                                             @PathVariable Long id) {
        return Result.success(refundService.getRefundDetail(userId, id));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approveRefund(@PathVariable Long id,
                                       @RequestParam(required = false) String remark) {
        refundService.approveRefund(id, remark);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> rejectRefund(@PathVariable Long id,
                                      @RequestParam String reason) {
        refundService.rejectRefund(id, reason);
        return Result.success();
    }
}
