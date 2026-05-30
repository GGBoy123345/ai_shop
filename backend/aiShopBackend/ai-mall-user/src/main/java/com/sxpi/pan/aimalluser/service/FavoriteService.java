package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimalluser.vo.FavoriteVO;

public interface FavoriteService {
    Page<FavoriteVO> getFavoriteList(Long userId, Integer page, Integer size);
    void addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
}
