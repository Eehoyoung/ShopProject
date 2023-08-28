package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    MyPageOrderStatusDto showOrderStatus(String loginId);

    OrderMainPageDto getOrderPagingDto(SearchOrder searchOrder, Pageable pageable, String loginId);

    void doOrder(Long id, List<Long> itemList, List<Integer> itemCountList, PaymentAddressDto paymentAddressDto, PaymentPriceDto paymentPriceDto, String payType);

    Page<OrderDto> findAllOrder(Pageable pageable);

    OrderPageDto findAllOrderByPaging(Pageable pageable);

    OrderPageDto findAllOrderByConditionByPaging(SearchOrder searchOrder, Pageable pageable);
}
