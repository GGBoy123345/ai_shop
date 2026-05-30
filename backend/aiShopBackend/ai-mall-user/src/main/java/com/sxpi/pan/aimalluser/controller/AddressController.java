package com.sxpi.pan.aimalluser.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.dto.AddressDTO;
import com.sxpi.pan.aimalluser.service.AddressService;
import com.sxpi.pan.aimalluser.vo.AddressVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<AddressVO>> getAddressList(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(addressService.getAddressList(userId));
    }

    @PostMapping
    public Result<Void> addAddress(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                    @Valid @RequestBody AddressDTO dto) {
        if (userId == null) return Result.error(401, "未登录");
        addressService.addAddress(userId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateAddress(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody AddressDTO dto) {
        if (userId == null) return Result.error(401, "未登录");
        addressService.updateAddress(userId, id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @PathVariable Long id) {
        if (userId == null) return Result.error(401, "未登录");
        addressService.deleteAddress(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                           @PathVariable Long id) {
        if (userId == null) return Result.error(401, "未登录");
        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }
}
