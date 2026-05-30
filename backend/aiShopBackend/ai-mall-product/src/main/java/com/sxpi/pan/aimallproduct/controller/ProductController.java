package com.sxpi.pan.aimallproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.dto.ProductDTO;
import com.sxpi.pan.aimallproduct.dto.ProductQueryDTO;
import com.sxpi.pan.aimallproduct.service.ProductService;
import com.sxpi.pan.aimallproduct.service.SkuService;
import com.sxpi.pan.aimallproduct.vo.ProductDetailVO;
import com.sxpi.pan.aimallproduct.vo.ProductVO;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SkuService skuService;

    @GetMapping
    public Result<Page<ProductVO>> getProductList(ProductQueryDTO query) {
        return Result.success(productService.getProductList(query));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @GetMapping("/{productId}/skus")
    public Result<List<SkuVO>> getProductSkus(@PathVariable Long productId) {
        return Result.success(skuService.getByProductId(productId));
    }

    @GetMapping("/merchant")
    public Result<Page<ProductVO>> getMerchantProducts(
            @RequestHeader("X-User-Id") Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.getMerchantProducts(merchantId, status, page, size));
    }

    @PostMapping
    public Result<Void> addProduct(
            @RequestHeader("X-User-Id") Long merchantId,
            @Valid @RequestBody ProductDTO dto) {
        productService.addProduct(merchantId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long merchantId,
            @Valid @RequestBody ProductDTO dto) {
        productService.updateProduct(id, merchantId, dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long merchantId,
            @RequestParam Integer status) {
        productService.updateProductStatus(id, merchantId, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long merchantId) {
        productService.deleteProduct(id, merchantId);
        return Result.success();
    }

    @PutMapping("/{id}/audit")
    public Result<Void> auditProduct(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        productService.auditProduct(id, status, remark);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Long> countProducts() {
        return Result.success(productService.countProducts());
    }
}
