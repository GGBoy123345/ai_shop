package com.sxpi.pan.aimalluser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.dto.MerchantApplyDTO;
import com.sxpi.pan.aimalluser.service.MerchantService;
import com.sxpi.pan.aimalluser.vo.MerchantVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/apply")
    public Result<Void> apply(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @Valid @RequestBody MerchantApplyDTO dto) {
        log.info("商家入驻申请, userId={}", userId);
        if (userId == null) return Result.error(401, "未登录");
        merchantService.apply(userId, dto);
        return Result.success();
    }

    @GetMapping("/me")
    public Result<MerchantVO> getMyMerchant(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("获取商家信息, userId={}", userId);
        if (userId == null) {
            return Result.success(null);
        }
        return Result.success(merchantService.getMerchantByUserId(userId));
    }

    @PutMapping("/me")
    public Result<Void> updateMyMerchant(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @Valid @RequestBody MerchantApplyDTO dto) {
        if (userId == null) return Result.error(401, "未登录");
        merchantService.updateMerchant(userId, dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MerchantVO> getMerchant(@PathVariable Long id) {
        return Result.success(merchantService.getMerchantById(id));
    }

    @GetMapping
    public Result<Page<MerchantVO>> getMerchantList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(merchantService.getMerchantList(status, page, size));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> auditMerchant(@PathVariable Long id,
                                       @RequestBody java.util.Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        String remark = (String) body.get("remark");
        merchantService.auditMerchant(id, status, remark);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateMerchantStatus(@PathVariable Long id,
                                              @RequestBody java.util.Map<String, Integer> body) {
        merchantService.updateMerchantStatus(id, body.get("status"));
        return Result.success();
    }
}
