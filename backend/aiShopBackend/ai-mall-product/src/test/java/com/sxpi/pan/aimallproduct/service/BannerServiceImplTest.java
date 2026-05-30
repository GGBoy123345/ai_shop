package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.BannerDTO;
import com.sxpi.pan.aimallproduct.entity.Banner;
import com.sxpi.pan.aimallproduct.mapper.BannerMapper;
import com.sxpi.pan.aimallproduct.service.impl.BannerServiceImpl;
import com.sxpi.pan.aimallproduct.vo.BannerVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BannerService 单元测试")
class BannerServiceImplTest {

    @InjectMocks
    private BannerServiceImpl bannerService;

    @Mock
    private BannerMapper bannerMapper;

    private Banner testBanner;

    @BeforeEach
    void setUp() {
        testBanner = new Banner();
        testBanner.setId(1L);
        testBanner.setImageUrl("http://example.com/banner1.jpg");
        testBanner.setLinkUrl("http://example.com/product/1");
        testBanner.setSort(1);
        testBanner.setStatus(1);
    }

    @Test
    @DisplayName("获取启用轮播图-成功")
    void getActiveBanners_success() {
        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testBanner));

        List<BannerVO> result = bannerService.getActiveBanners();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("http://example.com/banner1.jpg", result.get(0).getImageUrl());
    }

    @Test
    @DisplayName("添加轮播图-成功")
    void add_success() {
        when(bannerMapper.insert(any(Banner.class))).thenReturn(1);

        BannerDTO dto = new BannerDTO();
        dto.setImageUrl("http://example.com/new.jpg");
        dto.setLinkUrl("http://example.com");
        dto.setSort(1);

        assertDoesNotThrow(() -> bannerService.add(dto));
        verify(bannerMapper).insert(any(Banner.class));
    }

    @Test
    @DisplayName("更新轮播图-成功")
    void update_success() {
        when(bannerMapper.selectById(1L)).thenReturn(testBanner);
        when(bannerMapper.updateById(any(Banner.class))).thenReturn(1);

        BannerDTO dto = new BannerDTO();
        dto.setImageUrl("http://example.com/updated.jpg");

        assertDoesNotThrow(() -> bannerService.update(1L, dto));
        verify(bannerMapper).updateById(any(Banner.class));
    }

    @Test
    @DisplayName("更新轮播图-不存在")
    void update_notFound() {
        when(bannerMapper.selectById(999L)).thenReturn(null);

        BannerDTO dto = new BannerDTO();
        dto.setImageUrl("http://example.com/test.jpg");

        assertThrows(BusinessException.class, () -> bannerService.update(999L, dto));
    }

    @Test
    @DisplayName("删除轮播图-成功")
    void delete_success() {
        when(bannerMapper.selectById(1L)).thenReturn(testBanner);
        when(bannerMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> bannerService.delete(1L));
        verify(bannerMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除轮播图-不存在")
    void delete_notFound() {
        when(bannerMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> bannerService.delete(999L));
    }
}
