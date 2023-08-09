package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.dto.MyPageOrderStatusDto;
import com.shop.onlyfit.dto.OrderMainPageDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    MyPageOrderStatusDto showOrderStatus(String loginId);

    OrderMainPageDto getOrderPagingDto(SearchOrder searchOrder, Pageable pageable, String loginId);
}
