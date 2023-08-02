package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaidDto {
    private User userId;
    private Long payId;
    private String cusName; //구매자 이름
    private String itemName; //상품명
    private Long itemId;
    private int price;
    private int tid;
}
