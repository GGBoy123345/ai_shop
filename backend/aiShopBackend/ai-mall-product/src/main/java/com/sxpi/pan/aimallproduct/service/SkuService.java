package com.sxpi.pan.aimallproduct.service;

import com.sxpi.pan.aimallproduct.dto.SkuDTO;
import com.sxpi.pan.aimallproduct.vo.SkuVO;

import java.util.List;

public interface SkuService {
    List<SkuVO> getByProductId(Long productId);
    void add(Long productId, SkuDTO dto);
    void update(Long id, SkuDTO dto);
    void delete(Long id);
}
