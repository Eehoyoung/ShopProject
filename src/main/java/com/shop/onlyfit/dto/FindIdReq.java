package com.shop.onlyfit.dto;

import lombok.Getter;

@Getter
public class FindIdReq {
    private final String name;
    private final String phoneNum;

    public FindIdReq(String name, String phoneNum) {
        this.name = name;
        this.phoneNum = phoneNum;
    }
}
