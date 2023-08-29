package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.exception.MaxQuantityExceededException;
import com.shop.onlyfit.service.CartServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartApiController {

    private final CartServiceImpl cartService;
    private final UserServiceImpl userService;

    @PatchMapping("/main/cart/changequantity/{cartId}/{itemQuantity}")
    public String changeQuantityInCartPage(@PathVariable Long cartId, @PathVariable int itemQuantity) {
        try {
            cartService.changeCartItemQuantity(cartId, itemQuantity);
        } catch (MaxQuantityExceededException e) {
            return e.getMessage(); // "최대 수량을 초과하였습니다." 메시지를 반환합니다.
        } catch (Exception e) {
            return "에러 발생: " + e.getMessage();
        }

        return "수량 변경 완료";
    }

    @DeleteMapping("/main/cart/remove/{cartId}")
    public String deleteItemInCartPage(@PathVariable Long cartId) {
        cartService.deleteCartById(cartId);

        return "장바구니 상품 삭제 완료";
    }

    @DeleteMapping("/main/cart/removeitems")
    public String deleteItemsInBaketPage(@RequestParam(value = "itemList", required = false) List<Long> itemList) {
        for (Long aLong : itemList) {
            cartService.deleteCartById(aLong);
        }

        return "장바구니 상품 삭제 완료";
    }

    @DeleteMapping("/main/cart/removeall")
    public String deleteAllItemsInCartPage(Principal principal) {
        User user = userService.findUserByLoginId(principal.getName());
        List<Cart> cartList = cartService.findAllCartByUserId(user.getId());

        for (Cart cart : cartList) {
            cartService.deleteCartById(cart.getId());
        }

        return "장바구니 비우기 완료";
    }
}
