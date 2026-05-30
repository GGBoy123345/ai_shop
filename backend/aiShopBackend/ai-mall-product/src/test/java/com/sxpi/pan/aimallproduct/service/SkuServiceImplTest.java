package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.SkuDTO;
import com.sxpi.pan.aimallproduct.entity.Sku;
import com.sxpi.pan.aimallproduct.mapper.SkuMapper;
import com.sxpi.pan.aimallproduct.service.impl.SkuServiceImpl;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkuService 单元测试")
class SkuServiceImplTest {

    @InjectMocks
    private SkuServiceImpl skuService;

    @Mock
    private SkuMapper skuMapper;

    private Sku testSku;

    @BeforeEach
    void setUp() {
        testSku = new Sku();
        testSku.setId(1L);
        testSku.setProductId(1L);
        testSku.setPrice(new BigDecimal("99.00"));
        testSku.setStock(50);
        testSku.setAttributes("{\"颜色\":\"红色\",\"尺码\":\"M\"}");
    }

    @Test
    @DisplayName("根据商品ID查询SKU列表-成功")
    void getByProductId_success() {
        when(skuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testSku));

        List<SkuVO> result = skuService.getByProductId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("99.00"), result.get(0).getPrice());
    }

    @Test
    @DisplayName("添加SKU-成功")
    void add_success() {
        when(skuMapper.insert(any(Sku.class))).thenReturn(1);

        SkuDTO dto = new SkuDTO();
        dto.setPrice(new BigDecimal("199.00"));
        dto.setStock(100);
        dto.setAttributes("{\"颜色\":\"蓝色\"}");

        assertDoesNotThrow(() -> skuService.add(1L, dto));
        verify(skuMapper).insert(any(Sku.class));
    }

    @Test
    @DisplayName("更新SKU-成功")
    void update_success() {
        when(skuMapper.selectById(1L)).thenReturn(testSku);
        when(skuMapper.updateById(any(Sku.class))).thenReturn(1);

        SkuDTO dto = new SkuDTO();
        dto.setPrice(new BigDecimal("299.00"));
        dto.setStock(200);

        assertDoesNotThrow(() -> skuService.update(1L, dto));
        verify(skuMapper).updateById(any(Sku.class));
    }

    @Test
    @DisplayName("更新SKU-不存在")
    void update_notFound() {
        when(skuMapper.selectById(999L)).thenReturn(null);

        SkuDTO dto = new SkuDTO();
        dto.setPrice(new BigDecimal("99.00"));
        dto.setStock(10);

        assertThrows(BusinessException.class, () -> skuService.update(999L, dto));
    }

    @Test
    @DisplayName("删除SKU-成功")
    void delete_success() {
        when(skuMapper.selectById(1L)).thenReturn(testSku);
        when(skuMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> skuService.delete(1L));
        verify(skuMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除SKU-不存在")
    void delete_notFound() {
        when(skuMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> skuService.delete(999L));
    }
}
