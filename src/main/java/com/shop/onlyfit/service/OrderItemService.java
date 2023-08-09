package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.type.OrderStatus;

public interface OrderItemService {
    void changeOrderStatus(Long id, OrderStatus status);
}
