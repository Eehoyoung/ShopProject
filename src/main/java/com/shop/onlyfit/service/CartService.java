package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findAllCartByUserId(Long id);

    void changeCartItemQuantity(Long cartId, int itemQuantity);

    void deleteCartById(Long cartId);

}
