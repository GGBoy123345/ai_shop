package com.sxpi.pan.aimallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.BannerDTO;
import com.sxpi.pan.aimallproduct.entity.Banner;
import com.sxpi.pan.aimallproduct.mapper.BannerMapper;
import com.sxpi.pan.aimallproduct.service.BannerService;
import com.sxpi.pan.aimallproduct.vo.BannerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<BannerVO> getActiveBanners() {
        List<Banner> list = bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSort));
        return list.stream().map(b -> {
            BannerVO vo = new BannerVO();
            BeanUtils.copyProperties(b, vo);
            return vo;
        }).toList();
    }

    @Override
    public void add(BannerDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        banner.setStatus(1);
        bannerMapper.insert(banner);
    }

    @Override
    public void update(Long id, BannerDTO dto) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(40411, "轮播图不存在");
        }
        BeanUtils.copyProperties(dto, banner);
        bannerMapper.updateById(banner);
    }

    @Override
    public void delete(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(40411, "轮播图不存在");
        }
        bannerMapper.deleteById(id);
    }
}
