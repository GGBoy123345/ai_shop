package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.dto.BannerDTO;
import com.sxpi.pan.aimallproduct.service.BannerService;
import com.sxpi.pan.aimallproduct.vo.BannerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<List<BannerVO>> getActiveBanners() {
        return Result.success(bannerService.getActiveBanners());
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody BannerDTO dto) {
        bannerService.add(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BannerDTO dto) {
        bannerService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return Result.success();
    }
}
