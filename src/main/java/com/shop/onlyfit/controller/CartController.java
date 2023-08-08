package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.exception.MaxQuantityExceededException;
import com.shop.onlyfit.service.CartServiceImpl;
import com.shop.onlyfit.service.ItemServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {

    private final UserServiceImpl userService;
    private final CartServiceImpl cartService;
    private final ItemServiceImpl itemService;

    @Autowired
    public CartController(UserServiceImpl userService, CartServiceImpl cartService, ItemServiceImpl itemService) {
        this.userService = userService;
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @GetMapping("/main/cart")
    public String getCartPage(Principal principal, Model model) {
        String loginId = principal.getName();

        MyPageDto myPageDto = userService.showMySimpleInfo(loginId);
        User user = userService.findUserByLoginId(loginId);
        List<Cart> cartList = cartService.findAllCartByUserId(user.getId());
        List<ItemDto> allItemInCart = itemService.getAllItemInCart(cartList);

        model.addAttribute("user", myPageDto);
        model.addAttribute("itemList", allItemInCart);
        model.addAttribute("cartList", cartList);

        return "main/cart";
    }

    @ResponseBody
    @PatchMapping("/main/cart/changequantity/{cartId}/{itemQuantity}")
    public String changeQuantityInCartPage(@PathVariable Long cartId, @PathVariable int itemQuantity) {
        try{
            cartService.changeCartItemQuantity(cartId, itemQuantity);
        } catch (MaxQuantityExceededException e) {
            return e.getMessage(); // "최대 수량을 초과하였습니다." 메시지를 반환합니다.
        } catch (Exception e){
            return "에러 발생: " + e.getMessage();
        }

        return "수량 변경 완료";
    }

    @ResponseBody
    @DeleteMapping("/main/cart/remove/{cartId}")
    public String deleteItemInCartPage(@PathVariable Long cartId) {
        cartService.deleteCartById(cartId);

        return "장바구니 상품 삭제 완료";
    }

    @ResponseBody
    @DeleteMapping("/main/cart/removeitems")
    public String deleteItemsInBaketPage(@RequestParam(value = "itemList", required = false) List<Long> itemList) {
        for (Long aLong : itemList) {
            cartService.deleteCartById(aLong);
        }

        return "장바구니 상품 삭제 완료";
    }

    @ResponseBody
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
