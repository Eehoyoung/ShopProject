package com.shop.onlyfit.controller;

import com.shop.onlyfit.dto.item.ItemDetailDto;
import com.shop.onlyfit.dto.item.ItemToCartDto;
import com.shop.onlyfit.service.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProductController {

    private final ItemServiceImpl itemService;

    @Autowired
    public ProductController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/main/product/{itemIdx}")
    public String getProductPage(@PathVariable Long itemIdx, Model model) {
        ItemDetailDto itemDetailDto = itemService.getItemDetailDto(itemIdx);

        model.addAttribute("item", itemDetailDto);

        return "main/product";
    }

    @PostMapping("/main/product/basketadd_ok")
    public String addItemInCartPage(Principal principal, ItemToCartDto itemToCartDto) {
        int quantity = Integer.parseInt(itemToCartDto.getQuantity());
        Long itemIdx = Long.parseLong(itemToCartDto.getItem_idx());
        String color = itemToCartDto.getItem_color();

        itemService.moveItemToCart(principal.getName(), itemIdx, color, quantity);

        return "redirect:/main/cart";
    }
}
