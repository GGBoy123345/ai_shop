package com.sxpi.pan.aimallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.SkuDTO;
import com.sxpi.pan.aimallproduct.entity.Sku;
import com.sxpi.pan.aimallproduct.mapper.SkuMapper;
import com.sxpi.pan.aimallproduct.service.SkuService;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SkuMapper skuMapper;

    @Override
    public List<SkuVO> getByProductId(Long productId) {
        List<Sku> list = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, productId));
        return list.stream().map(s -> {
            SkuVO vo = new SkuVO();
            BeanUtils.copyProperties(s, vo);
            return vo;
        }).toList();
    }

    @Override
    public void add(Long productId, SkuDTO dto) {
        Sku sku = new Sku();
        BeanUtils.copyProperties(dto, sku);
        sku.setProductId(productId);
        skuMapper.insert(sku);
    }

    @Override
    public void update(Long id, SkuDTO dto) {
        Sku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(40413, "SKU不存在");
        }
        BeanUtils.copyProperties(dto, sku);
        skuMapper.updateById(sku);
    }

    @Override
    public void delete(Long id) {
        Sku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(40413, "SKU不存在");
        }
        skuMapper.deleteById(id);
    }
}
