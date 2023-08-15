package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.dto.MyPageOrderStatusDto;
import com.shop.onlyfit.dto.OrderMainPageDto;
import com.shop.onlyfit.dto.PaymentAddressDto;
import com.shop.onlyfit.dto.PaymentPriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    MyPageOrderStatusDto showOrderStatus(String loginId);

    OrderMainPageDto getOrderPagingDto(SearchOrder searchOrder, Pageable pageable, String loginId);

    void doOrder(Long id, List<Long> itemList, List<Integer> itemCountList, PaymentAddressDto paymentAddressDto, PaymentPriceDto paymentPriceDto, String payType);

}
