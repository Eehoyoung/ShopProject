package com.shop.onlyfit.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ChatPageDto {
    Page<ChatDslDto> chatBoards;
    int startPage;
    int endPage;
}
