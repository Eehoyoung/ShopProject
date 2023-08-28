package com.shop.onlyfit.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.onlyfit.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatDslDto {

    private Long roomId;
    private User sender;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @QueryProjection
    public ChatDslDto(Long roomId, User sender, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.roomId = roomId;
        this.sender = sender;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
