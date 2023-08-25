package com.shop.onlyfit.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ChatDto {
    @Getter
    public static class Post {
        @NotNull
        private long userId; // 채팅을 받는 사람
    }

    @Getter
    @Builder
    public static class RoomResponse {
        private long roomId;
        private ChatUserDto.ResponseOnlyUserName sender;
        private ChatUserDto.ResponseOnlyUserName receiver;
    }

    @Getter
    @Builder
    public static class MessageResponse { // 채팅방 속 하나의 메세지
        private long messageId;
        private ChatUserDto.ResponseOnlyUserName sender;
        private String content;
        private LocalDateTime sendTime;
        private boolean success;
    }
}