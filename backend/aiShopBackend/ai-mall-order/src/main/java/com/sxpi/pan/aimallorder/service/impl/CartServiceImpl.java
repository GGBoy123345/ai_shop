package com.sxpi.pan.aimallorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallorder.dto.CartDTO;
import com.sxpi.pan.aimallorder.entity.Cart;
import com.sxpi.pan.aimallorder.mapper.CartMapper;
import com.sxpi.pan.aimallorder.service.CartService;
import com.sxpi.pan.aimallorder.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<Cart> list = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .orderByDesc(Cart::getCreateTime));
        return list.stream().map(c -> {
            CartVO vo = new CartVO();
            vo.setId(c.getId());
            vo.setProductId(c.getProductId());
            vo.setSkuId(c.getSkuId());
            vo.setQuantity(c.getQuantity());
            vo.setChecked(c.getChecked());
            // 商品信息需要从product-svc获取，这里先设默认值
            vo.setProductTitle("商品#" + c.getProductId());
            vo.setPrice(BigDecimal.ZERO);
            return vo;
        }).toList();
    }

    @Override
    public void addToCart(Long userId, CartDTO dto) {
        // 检查购物车是否已有该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, dto.getProductId());
        if (dto.getSkuId() != null) {
            wrapper.eq(Cart::getSkuId, dto.getSkuId());
        }
        Cart existing = cartMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + (dto.getQuantity() != null ? dto.getQuantity() : 1));
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(dto.getProductId());
            cart.setSkuId(dto.getSkuId());
            cart.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(40420, "购物车项不存在");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void updateChecked(Long userId, Long cartId, Integer checked) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(40420, "购物车项不存在");
        }
        cart.setChecked(checked);
        cartMapper.updateById(cart);
    }

    @Override
    public void deleteItem(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(40420, "购物车项不存在");
        }
        cartMapper.deleteById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }
}
