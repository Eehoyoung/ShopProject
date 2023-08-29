package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.service.CartServiceImpl;
import com.shop.onlyfit.service.ItemServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final UserServiceImpl userService;
    private final CartServiceImpl cartService;
    private final ItemServiceImpl itemService;

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
}
