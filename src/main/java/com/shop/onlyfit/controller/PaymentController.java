package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.DeliveryAddress;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.AddressChangeDto;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.PaymentAddressDto;
import com.shop.onlyfit.dto.PaymentPriceDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemListToOrderDto;
import com.shop.onlyfit.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PaymentController {

    private final CartServiceImpl cartService;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final DeliveryAddressServiceImpl deliveryAddressService;
    private final OrderServiceImpl orderService;

    @Autowired
    public PaymentController(CartServiceImpl cartService, ItemServiceImpl itemService, UserServiceImpl userService, DeliveryAddressServiceImpl deliveryAddressService, OrderServiceImpl orderService) {
        this.cartService = cartService;
        this.itemService = itemService;
        this.userService = userService;
        this.deliveryAddressService = deliveryAddressService;
        this.orderService = orderService;
    }

    @PostMapping("/main/payment")
    public String getPaymentDataPage(Principal principal, Model model, @RequestParam(value = "itemlist") String itemlist, @RequestParam(value = "where") String where) {

        System.out.println("있어?" + principal.getName());
        if (principal.getName() == null) {
            return "redirect:/main/index";
        }

        List<ItemDto> itemDtoList = new ArrayList<>();
        if (where.equals("cart")) {
            itemDtoList = cartService.cartListToPayment(itemlist);
        } else if (where.equals("product")) {
            itemDtoList = itemService.itemToPayment(itemlist);
        }

        String loginId = principal.getName();
        MyPageDto myPageDto = userService.showMySimpleInfo(loginId);
        List<DeliveryAddress> deliveryAddressList = deliveryAddressService.getDeliveryAddressByLoginId(loginId);

        model.addAttribute("orderList", itemDtoList);
        model.addAttribute("addressList", deliveryAddressList);
        model.addAttribute("user", myPageDto);

        return "main/payment";
    }

    @ResponseBody
    @PostMapping("/main/payment/changeaddress/{deliveryAddressId}")
    public AddressChangeDto chnageDeliveryAddressInfo(@PathVariable Long deliveryAddressId) {

        return deliveryAddressService.showAddressToChange(deliveryAddressId);
    }

    @PostMapping("main/payment_ok")
    public String doPaymentPage(Principal principal, @RequestParam(value = "orderiteminfo") String orderItemInfo,
                                @RequestParam(value = "paytype", defaultValue = "vBank") String payType,
                                PaymentAddressDto paymentAddressDto, PaymentPriceDto paymentPriceDto, Model model) {
        User user = userService.findUserByLoginId(principal.getName());
        ItemListToOrderDto itemListToOrderDto = itemService.itemToOrder(orderItemInfo);
        MyPageDto myPageDto = userService.showMySimpleInfo(principal.getName());
        paymentPriceDto.setEarnMileage(paymentPriceDto.getTobepaid_price() * 0.01);

        orderService.doOrder
                (user.getId(), itemListToOrderDto.getItemList(),
                        itemListToOrderDto.getItemCountList(),
                        paymentAddressDto, paymentPriceDto, payType);

        model.addAttribute("user", myPageDto);
        model.addAttribute("payment", paymentPriceDto);
        model.addAttribute("address", paymentAddressDto);

        return "main/payment_complete";
    }
}
