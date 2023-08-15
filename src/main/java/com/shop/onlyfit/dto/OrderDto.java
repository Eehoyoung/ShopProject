package com.shop.onlyfit.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.domain.type.PostCompany;
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
    private String postNumber;
    private OrderStatus orderStatus;
    private PostCompany postCompany;
    private Long marketId;

    @QueryProjection
    public OrderDto(Long id, Long orderItemId, String name, String itemName, LocalDate orderedAt, String payment, int orderPrice, OrderStatus orderStatus, Long marketId, PostCompany postCompany, String postNumber) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.name = name;
        this.itemName = itemName;
        this.orderedAt = orderedAt;
        this.payment = payment;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
        this.marketId = marketId;
        this.postCompany = postCompany;
        this.postNumber = postNumber;
    }
}
