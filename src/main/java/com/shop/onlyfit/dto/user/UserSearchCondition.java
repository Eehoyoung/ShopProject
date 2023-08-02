package com.shop.onlyfit.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchCondition {
    private String name;
    private String loginId;

    public UserSearchCondition(String name, String loginId) {
        this.name = name;
        this.loginId = loginId;
    }

}
