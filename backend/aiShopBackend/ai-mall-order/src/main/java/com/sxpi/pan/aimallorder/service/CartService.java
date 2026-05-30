package com.sxpi.pan.aimallorder.service;

import com.sxpi.pan.aimallorder.dto.CartDTO;
import com.sxpi.pan.aimallorder.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> getCartList(Long userId);
    void addToCart(Long userId, CartDTO dto);
    void updateQuantity(Long userId, Long cartId, Integer quantity);
    void updateChecked(Long userId, Long cartId, Integer checked);
    void deleteItem(Long userId, Long cartId);
    void clearCart(Long userId);
}
