package com.shop.onlyfit.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserGrade {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SELLER("ROLE_SELLER");

    private String value;
}

