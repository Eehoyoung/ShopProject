package com.shop.onlyfit.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CSBoardType {

    NORMAL("ROLE_NORMAL"),
    NOTICE("ROLE_NOTICE");

    private String value;
}
