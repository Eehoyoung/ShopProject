package com.shop.onlyfit.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.onlyfit.domain.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long orderItemId;
    private String name;
    private String itemName;
    private LocalDate orderedAt;
    private String payment;
    private int orderPrice;
    private OrderStatus orderStatus;
    private Long marketId;

    @QueryProjection
    public OrderDto(Long id, Long orderItemId, String name, String itemName, LocalDate orderedAt, String payment, int orderPrice, OrderStatus orderStatus, Long marketId) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.name = name;
        this.itemName = itemName;
        this.orderedAt = orderedAt;
        this.payment = payment;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
        this.marketId = marketId;
    }
}
