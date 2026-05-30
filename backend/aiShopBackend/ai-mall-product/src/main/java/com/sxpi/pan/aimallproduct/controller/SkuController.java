package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.dto.SkuDTO;
import com.sxpi.pan.aimallproduct.service.SkuService;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skus")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @PostMapping
    public Result<Void> add(@RequestParam Long productId, @Valid @RequestBody SkuDTO dto) {
        skuService.add(productId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SkuDTO dto) {
        skuService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skuService.delete(id);
        return Result.success();
    }
}
