package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.entity.Product;
import com.sxpi.pan.aimallproduct.entity.ProductAttribute;
import com.sxpi.pan.aimallproduct.entity.Sku;
import com.sxpi.pan.aimallproduct.mapper.ProductAttributeMapper;
import com.sxpi.pan.aimallproduct.mapper.ProductMapper;
import com.sxpi.pan.aimallproduct.mapper.SkuMapper;
import com.sxpi.pan.aimallproduct.vo.ProductDetailVO;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;
    private final ProductAttributeMapper attributeMapper;

    @GetMapping("/products/{id}")
    public Result<Product> getProductById(@PathVariable Long id,
                                          @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        return Result.success(product);
    }

    @GetMapping("/skus/{id}")
    public Result<SkuVO> getSkuById(@PathVariable Long id,
                                    @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Sku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(40413, "SKU不存在");
        }
        SkuVO vo = new SkuVO();
        org.springframework.beans.BeanUtils.copyProperties(sku, vo);
        return Result.success(vo);
    }

    @PutMapping("/skus/{id}/stock/deduct")
    public Result<Void> deductStock(@PathVariable Long id,
                                    @RequestParam Integer quantity,
                                    @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Sku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(40413, "SKU不存在");
        }
        if (sku.getStock() < quantity) {
            throw new BusinessException(40033, "库存不足");
        }
        skuMapper.update(null, new LambdaUpdateWrapper<Sku>()
                .eq(Sku::getId, id)
                .set(Sku::getStock, sku.getStock() - quantity));
        return Result.success();
    }

    @PutMapping("/skus/{id}/stock/restore")
    public Result<Void> restoreStock(@PathVariable Long id,
                                     @RequestParam Integer quantity,
                                     @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        Sku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(40413, "SKU不存在");
        }
        skuMapper.update(null, new LambdaUpdateWrapper<Sku>()
                .eq(Sku::getId, id)
                .set(Sku::getStock, sku.getStock() + quantity));
        return Result.success();
    }

    @GetMapping("/products/{id}/attributes")
    public Result<List<ProductAttribute>> getProductAttributes(@PathVariable Long id,
                                                                @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        validateToken(token);
        List<ProductAttribute> attrs = attributeMapper.selectList(
                new LambdaQueryWrapper<ProductAttribute>()
                        .eq(ProductAttribute::getProductId, id));
        return Result.success(attrs);
    }

    private void validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "内部调用令牌缺失");
        }
    }
}
