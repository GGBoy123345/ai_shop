package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.entity.Favorite;
import com.sxpi.pan.aimalluser.mapper.FavoriteMapper;
import com.sxpi.pan.aimalluser.service.FavoriteService;
import com.sxpi.pan.aimalluser.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    public Page<FavoriteVO> getFavoriteList(Long userId, Integer page, Integer size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);
        Page<Favorite> result = favoriteMapper.selectPage(pageParam, wrapper);

        Page<FavoriteVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(f -> {
            FavoriteVO vo = new FavoriteVO();
            vo.setId(f.getId());
            vo.setProductId(f.getProductId());
            vo.setCreateTime(f.getCreateTime() != null ? f.getCreateTime().toString() : null);
            return vo;
        }).toList());
        return voPage;
    }

    @Override
    public void addFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(40028, "该商品已收藏");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }
}
