package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.DeliveryAddress;
import com.shop.onlyfit.dto.AddressChangeDto;
import com.shop.onlyfit.dto.AddressDto;

import java.util.List;

public interface deliveryAddressService {
    List<DeliveryAddress> getDeliveryAddressByLoginId(String loginId);

    void registerAddress(String loginId, AddressDto addressDto);

    void deleteAddressById(Long addressId);

    AddressChangeDto showAddressToChange(Long id);

    void updateDeliveryAddress(Long id, AddressChangeDto addressChangeDto);
}
