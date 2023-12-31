package com.shop.onlyfit.domain.type;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.shop.onlyfit.domain.QOrderItem;

import java.util.function.Function;

public enum OrderStatus {
    PAYWAITING(
            str ->
                    QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    PAYCOMPLETE(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    PRESHIP(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    INSHIP(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    COMPLETESHIP(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    ORDERCANCEL(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    ORDERCHANGE(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str))),
    ORDERREFUND(str -> QOrderItem.orderItem.orderStatus.eq(OrderStatus.valueOf(str)));
    //준비중,진행중 ,취소

    private final Function<String, BooleanExpression> expression;

    OrderStatus(Function<String, BooleanExpression> expression) {
        this.expression = expression;
    }

}
