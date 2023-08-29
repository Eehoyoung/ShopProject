package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.DeliveryAddress;
import com.shop.onlyfit.dto.AddressChangeDto;
import com.shop.onlyfit.dto.AddressDto;
import com.shop.onlyfit.service.DeliveryAddressServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final DeliveryAddressServiceImpl deliveryAddressService;

    @GetMapping("/main/address")
    public String getAddressPage(Principal principal, Model model) {
        String loginId = principal.getName();
        List<DeliveryAddress> deliveryAddressList = deliveryAddressService.getDeliveryAddressByLoginId(loginId);

        model.addAttribute("addressList", deliveryAddressList);

        return "main/address";
    }

    @GetMapping("/main/address/register")
    public String getRegisterPage() {
        return "main/register_address";
    }

    @PostMapping("/main/address/register_ok")
    public String registerAddressPage(Principal principal, @ModelAttribute AddressDto addressDto) {
        String loginId = principal.getName();

        deliveryAddressService.registerAddress(loginId, addressDto);

        return "redirect:/main/address";
    }

    @GetMapping("/main/address/change/{id}")
    public String getChangeAddressPage(@PathVariable Long id, Model model) {
        AddressChangeDto addressChangeDto = deliveryAddressService.showAddressToChange(id);

        model.addAttribute("address", addressChangeDto);

        return "main/change_address";
    }

    @PutMapping("/main/changeaddress_ok")
    public String changeAddressStatus(@ModelAttribute AddressChangeDto addressChangeDto) {

        deliveryAddressService.updateDeliveryAddress(addressChangeDto.getId(), addressChangeDto);

        return "redirect:/main/address";
    }

}
