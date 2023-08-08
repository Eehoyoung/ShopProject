package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.exception.CartNotFoundException;
import com.shop.onlyfit.exception.MaxQuantityExceededException;
import com.shop.onlyfit.repository.CartRepository;
import com.shop.onlyfit.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartServiceImpl(CartRepository cartRepository, ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Cart> findAllCartByUserId(Long id) {
        return cartRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    public void changeCartItemQuantity(Long cartId, int itemQuantity) throws MaxQuantityExceededException {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CartNotFoundException("해당하는 장바구니를 찾을 수 없습니다.")
        );
        int maxItemQuantity = itemRepository.findMaxItemQuantity(cart.getItem().getId());
        if(itemQuantity > maxItemQuantity){
            throw new MaxQuantityExceededException("최대 수량을 초과하였습니다.");
        }
        cart.setCartCount(itemQuantity);
    }

    @Override
    @Transactional
    public void deleteCartById(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
