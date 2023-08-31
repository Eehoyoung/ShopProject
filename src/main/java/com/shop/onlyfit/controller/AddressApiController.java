package com.shop.onlyfit.controller;

import com.shop.onlyfit.service.DeliveryAddressServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressApiController {

    private final DeliveryAddressServiceImpl deliveryAddressService;

    @ApiOperation("Delete address")
    @DeleteMapping("/main/address/delete")
    public String deleteAddressPage(@RequestParam(value = "addressIdList", required = false) List<Long> addressIdList) {
        for (Long addressId : addressIdList) {
            deliveryAddressService.deleteAddressById(addressId);
        }
        return "주소 삭제 완료";
    }
}
