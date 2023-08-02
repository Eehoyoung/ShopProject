package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfo {
    private String name;

    private OrderStatus orderStatus;

    private String itemName;

    private int orderPrice;
}
