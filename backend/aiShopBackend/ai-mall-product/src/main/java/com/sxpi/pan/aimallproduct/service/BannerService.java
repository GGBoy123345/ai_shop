package com.sxpi.pan.aimallproduct.service;

import com.sxpi.pan.aimallproduct.dto.BannerDTO;
import com.sxpi.pan.aimallproduct.vo.BannerVO;

import java.util.List;

public interface BannerService {
    List<BannerVO> getActiveBanners();
    void add(BannerDTO dto);
    void update(Long id, BannerDTO dto);
    void delete(Long id);
}
