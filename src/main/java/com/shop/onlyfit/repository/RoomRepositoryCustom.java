package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.SearchChat;
import com.shop.onlyfit.dto.ChatDslDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {

    Page<ChatDslDto> searchAllChat(Pageable pageable);

    Page<ChatDslDto> searchByConditionAllChat(SearchChat searchChat, Pageable pageable);
}
