package com.shop.onlyfit.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.exception.CartNotFoundException;
import com.shop.onlyfit.exception.MaxQuantityExceededException;
import com.shop.onlyfit.repository.CartRepository;
import com.shop.onlyfit.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

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
        if (itemQuantity > maxItemQuantity) {
            throw new MaxQuantityExceededException("최대 수량을 초과하였습니다.");
        }
        cart.setCartCount(itemQuantity);
    }

    @Override
    @Transactional
    public void deleteCartById(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public List<ItemDto> cartListToPayment(String itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        JsonArray jsonArray = new Gson().fromJson(itemList, JsonArray.class);
        List<Long> cartIdList = new ArrayList<>();
        List<Integer> cartQuantityList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject object = (JsonObject) jsonArray.get(i);
            String id = object.get("id").getAsString();
            String quantity = object.get("quantity").getAsString();

            cartIdList.add(Long.parseLong(id));
            cartQuantityList.add(Integer.parseInt(quantity));
        }

        for (Long aLong : cartIdList) {
            Optional<Cart> byId = cartRepository.findById(aLong);
            Cart cart = byId.orElseThrow(
                    () -> new CartNotFoundException("해당하는 장바구니가 존재하지 않습니다.")
            );
            Long itemId = cart.getItem().getId();

            itemDtoList.add(itemRepository.findAllItemInCart(itemId));
        }

        return itemDtoList;
    }
}
