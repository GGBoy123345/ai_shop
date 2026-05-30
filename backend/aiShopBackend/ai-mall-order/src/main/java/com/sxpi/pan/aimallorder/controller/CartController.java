package com.sxpi.pan.aimallorder.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallorder.dto.CartDTO;
import com.sxpi.pan.aimallorder.service.CartService;
import com.sxpi.pan.aimallorder.vo.CartVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartVO>> getCartList(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(cartService.getCartList(userId));
    }

    @PostMapping
    public Result<Void> addToCart(@RequestHeader("X-User-Id") Long userId,
                                  @Valid @RequestBody CartDTO dto) {
        cartService.addToCart(userId, dto);
        return Result.success();
    }

    @PutMapping("/{id}/quantity")
    public Result<Void> updateQuantity(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable Long id,
                                        @RequestParam Integer quantity) {
        cartService.updateQuantity(userId, id, quantity);
        return Result.success();
    }

    @PutMapping("/{id}/checked")
    public Result<Void> updateChecked(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable Long id,
                                       @RequestParam Integer checked) {
        cartService.updateChecked(userId, id, checked);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long id) {
        cartService.deleteItem(userId, id);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return Result.success();
    }
}
