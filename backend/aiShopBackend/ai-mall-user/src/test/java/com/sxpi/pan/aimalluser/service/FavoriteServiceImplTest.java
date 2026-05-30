package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.entity.Favorite;
import com.sxpi.pan.aimalluser.mapper.FavoriteMapper;
import com.sxpi.pan.aimalluser.service.impl.FavoriteServiceImpl;
import com.sxpi.pan.aimalluser.vo.FavoriteVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private Favorite buildFavorite(Long id, Long userId, Long productId) {
        Favorite f = new Favorite();
        f.setId(id);
        f.setUserId(userId);
        f.setProductId(productId);
        f.setCreateTime(LocalDateTime.now());
        return f;
    }

    @Test
    void getFavoriteList_success() {
        Page<Favorite> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(buildFavorite(1L, 100L, 200L)));
        page.setTotal(1);
        when(favoriteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<FavoriteVO> result = favoriteService.getFavoriteList(100L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(200L, result.getRecords().get(0).getProductId());
    }

    @Test
    void getFavoriteList_empty() {
        Page<Favorite> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList());
        page.setTotal(0);
        when(favoriteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<FavoriteVO> result = favoriteService.getFavoriteList(100L, 1, 10);

        assertEquals(0, result.getRecords().size());
    }

    @Test
    void addFavorite_success() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(favoriteMapper.insert(any(Favorite.class))).thenReturn(1);

        assertDoesNotThrow(() -> favoriteService.addFavorite(100L, 200L));
        verify(favoriteMapper).insert(any(Favorite.class));
    }

    @Test
    void addFavorite_alreadyExists() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> favoriteService.addFavorite(100L, 200L));
        assertEquals(40028, ex.getCode());
    }

    @Test
    void removeFavorite_success() {
        when(favoriteMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        assertDoesNotThrow(() -> favoriteService.removeFavorite(100L, 200L));
        verify(favoriteMapper).delete(any(LambdaQueryWrapper.class));
    }
}
