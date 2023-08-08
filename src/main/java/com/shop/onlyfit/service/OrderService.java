package com.shop.onlyfit.service;

import com.shop.onlyfit.dto.MyPageOrderStatusDto;

public interface OrderService {

    MyPageOrderStatusDto showOrderStatus(String loginId);

}
