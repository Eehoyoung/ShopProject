package com.shop.onlyfit.dto;

import lombok.Builder;
import lombok.Getter;

public class ChatUserDto {
    @Getter
    @Builder
    public static class ResponseOnlyUserName {
        private long userId;
        private String nickName;
    }
}

