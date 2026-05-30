package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallorder.dto.CartDTO;
import com.sxpi.pan.aimallorder.entity.Cart;
import com.sxpi.pan.aimallorder.mapper.CartMapper;
import com.sxpi.pan.aimallorder.service.impl.CartServiceImpl;
import com.sxpi.pan.aimallorder.vo.CartVO;
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
@DisplayName("CartService 单元测试")
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartMapper cartMapper;

    private Cart testCart;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(userId);
        testCart.setProductId(100L);
        testCart.setSkuId(200L);
        testCart.setQuantity(2);
        testCart.setChecked(1);
    }

    @Test
    @DisplayName("获取购物车列表-成功")
    void getCartList_success() {
        when(cartMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testCart));

        List<CartVO> result = cartService.getCartList(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getProductId());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    @DisplayName("获取购物车列表-空")
    void getCartList_empty() {
        when(cartMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CartVO> result = cartService.getCartList(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("添加购物车-新商品")
    void addToCart_newItem() {
        when(cartMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        CartDTO dto = new CartDTO();
        dto.setProductId(101L);
        dto.setQuantity(1);

        assertDoesNotThrow(() -> cartService.addToCart(userId, dto));
        verify(cartMapper).insert(any(Cart.class));
    }

    @Test
    @DisplayName("添加购物车-已有商品合并数量")
    void addToCart_existingItem() {
        when(cartMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCart);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        CartDTO dto = new CartDTO();
        dto.setProductId(100L);
        dto.setSkuId(200L);
        dto.setQuantity(3);

        assertDoesNotThrow(() -> cartService.addToCart(userId, dto));
        assertEquals(5, testCart.getQuantity());
        verify(cartMapper).updateById(testCart);
    }

    @Test
    @DisplayName("添加购物车-已有商品默认数量+1")
    void addToCart_existingItemDefaultQuantity() {
        when(cartMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCart);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        CartDTO dto = new CartDTO();
        dto.setProductId(100L);
        dto.setSkuId(200L);
        dto.setQuantity(null);

        assertDoesNotThrow(() -> cartService.addToCart(userId, dto));
        assertEquals(3, testCart.getQuantity());
    }

    @Test
    @DisplayName("更新数量-成功")
    void updateQuantity_success() {
        when(cartMapper.selectById(1L)).thenReturn(testCart);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        assertDoesNotThrow(() -> cartService.updateQuantity(userId, 1L, 5));
        assertEquals(5, testCart.getQuantity());
    }

    @Test
    @DisplayName("更新数量-购物车项不存在")
    void updateQuantity_notFound() {
        when(cartMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.updateQuantity(userId, 999L, 5));
        assertEquals(40420, ex.getCode());
    }

    @Test
    @DisplayName("更新数量-无权操作")
    void updateQuantity_wrongUser() {
        when(cartMapper.selectById(1L)).thenReturn(testCart);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.updateQuantity(999L, 1L, 5));
        assertEquals(40420, ex.getCode());
    }

    @Test
    @DisplayName("更新选中状态-成功")
    void updateChecked_success() {
        when(cartMapper.selectById(1L)).thenReturn(testCart);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        assertDoesNotThrow(() -> cartService.updateChecked(userId, 1L, 0));
        assertEquals(0, testCart.getChecked());
    }

    @Test
    @DisplayName("更新选中状态-购物车项不存在")
    void updateChecked_notFound() {
        when(cartMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.updateChecked(userId, 999L, 0));
        assertEquals(40420, ex.getCode());
    }

    @Test
    @DisplayName("删除购物车项-成功")
    void deleteItem_success() {
        when(cartMapper.selectById(1L)).thenReturn(testCart);
        when(cartMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> cartService.deleteItem(userId, 1L));
        verify(cartMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除购物车项-不存在")
    void deleteItem_notFound() {
        when(cartMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.deleteItem(userId, 999L));
        assertEquals(40420, ex.getCode());
    }

    @Test
    @DisplayName("删除购物车项-无权操作")
    void deleteItem_wrongUser() {
        when(cartMapper.selectById(1L)).thenReturn(testCart);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.deleteItem(999L, 1L));
        assertEquals(40420, ex.getCode());
    }

    @Test
    @DisplayName("清空购物车-成功")
    void clearCart_success() {
        when(cartMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(3);

        assertDoesNotThrow(() -> cartService.clearCart(userId));
        verify(cartMapper).delete(any(LambdaQueryWrapper.class));
    }
}
