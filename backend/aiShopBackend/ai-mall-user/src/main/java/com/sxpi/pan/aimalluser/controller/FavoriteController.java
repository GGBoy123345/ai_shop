package com.sxpi.pan.aimalluser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.service.FavoriteService;
import com.sxpi.pan.aimalluser.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<Page<FavoriteVO>> getFavoriteList(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(favoriteService.getFavoriteList(userId, page, size));
    }

    @PostMapping("/{productId}")
    public Result<Void> addFavorite(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                     @PathVariable Long productId) {
        if (userId == null) return Result.error(401, "未登录");
        favoriteService.addFavorite(userId, productId);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> removeFavorite(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @PathVariable Long productId) {
        if (userId == null) return Result.error(401, "未登录");
        favoriteService.removeFavorite(userId, productId);
        return Result.success();
    }
}
