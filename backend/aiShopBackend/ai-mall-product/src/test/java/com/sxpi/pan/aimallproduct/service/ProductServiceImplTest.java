package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.ProductDTO;
import com.sxpi.pan.aimallproduct.dto.ProductQueryDTO;
import com.sxpi.pan.aimallproduct.entity.Product;
import com.sxpi.pan.aimallproduct.entity.ProductAttribute;
import com.sxpi.pan.aimallproduct.entity.Sku;
import com.sxpi.pan.aimallproduct.entity.AttributeTemplate;
import com.sxpi.pan.aimallproduct.mapper.ProductAttributeMapper;
import com.sxpi.pan.aimallproduct.mapper.ProductMapper;
import com.sxpi.pan.aimallproduct.mapper.SkuMapper;
import com.sxpi.pan.aimallproduct.mapper.AttributeTemplateMapper;
import com.sxpi.pan.aimallproduct.service.impl.ProductServiceImpl;
import com.sxpi.pan.aimallproduct.vo.ProductDetailVO;
import com.sxpi.pan.aimallproduct.vo.ProductVO;
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
@DisplayName("ProductService 单元测试")
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductAttributeMapper attributeMapper;
    @Mock
    private SkuMapper skuMapper;
    @Mock
    private AttributeTemplateMapper templateMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setMerchantId(10L);
        testProduct.setCategoryId(1L);
        testProduct.setTitle("测试商品");
        testProduct.setPrice(new BigDecimal("99.00"));
        testProduct.setStock(100);
        testProduct.setSales(10);
        testProduct.setStatus(1);
    }

    @Test
    @DisplayName("获取商品列表-成功")
    void getProductList_success() {
        Page<Product> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testProduct));
        mockPage.setTotal(1);

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        ProductQueryDTO query = new ProductQueryDTO();
        Page<ProductVO> result = productService.getProductList(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("测试商品", result.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("获取商品详情-成功")
    void getProductDetail_success() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(attributeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(skuMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        ProductDetailVO detail = productService.getProductDetail(1L);

        assertNotNull(detail);
        assertEquals("测试商品", detail.getTitle());
        assertEquals(new BigDecimal("99.00"), detail.getPrice());
    }

    @Test
    @DisplayName("获取商品详情-不存在")
    void getProductDetail_notFound() {
        when(productMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> productService.getProductDetail(999L));
    }

    @Test
    @DisplayName("添加商品-成功")
    void addProduct_success() {
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        ProductDTO dto = new ProductDTO();
        dto.setCategoryId(1L);
        dto.setTitle("新商品");
        dto.setPrice(new BigDecimal("199.00"));

        assertDoesNotThrow(() -> productService.addProduct(10L, dto));
        verify(productMapper).insert(any(Product.class));
    }

    @Test
    @DisplayName("更新商品-成功")
    void updateProduct_success() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);
        when(attributeMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

        ProductDTO dto = new ProductDTO();
        dto.setCategoryId(1L);
        dto.setTitle("更新后的商品");
        dto.setPrice(new BigDecimal("299.00"));

        assertDoesNotThrow(() -> productService.updateProduct(1L, 10L, dto));
        verify(productMapper).updateById(any(Product.class));
    }

    @Test
    @DisplayName("更新商品-无权操作")
    void updateProduct_noPermission() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        ProductDTO dto = new ProductDTO();
        dto.setTitle("测试");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.updateProduct(1L, 999L, dto));
        assertEquals(40302, ex.getCode());
    }

    @Test
    @DisplayName("更新商品状态-成功")
    void updateProductStatus_success() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        assertDoesNotThrow(() -> productService.updateProductStatus(1L, 10L, 0));
        assertEquals(0, testProduct.getStatus());
    }

    @Test
    @DisplayName("删除商品-成功")
    void deleteProduct_success() {
        testProduct.setStatus(0);
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> productService.deleteProduct(1L, 10L));
        verify(productMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除商品-非下架状态")
    void deleteProduct_notOffShelf() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.deleteProduct(1L, 10L));
        assertEquals(40031, ex.getCode());
    }

    @Test
    @DisplayName("审核商品-通过")
    void auditProduct_approve() {
        testProduct.setStatus(2);
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        assertDoesNotThrow(() -> productService.auditProduct(1L, 1, "审核通过"));
        assertEquals(1, testProduct.getStatus());
    }

    @Test
    @DisplayName("审核商品-不在待审核状态")
    void auditProduct_notPending() {
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.auditProduct(1L, 1, "审核通过"));
        assertEquals(40032, ex.getCode());
    }

    @Test
    @DisplayName("获取商家商品列表-成功")
    void getMerchantProducts_success() {
        Page<Product> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testProduct));
        mockPage.setTotal(1);

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<ProductVO> result = productService.getMerchantProducts(10L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }
}
