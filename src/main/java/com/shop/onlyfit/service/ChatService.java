package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchChat;
import com.shop.onlyfit.domain.chat.ChatRoom;
import com.shop.onlyfit.dto.ChatDslDto;
import com.shop.onlyfit.dto.ChatDto;
import com.shop.onlyfit.dto.ChatPageDto;
import com.shop.onlyfit.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatService {

    @Transactional
    List<ChatDto.MessageResponse> getPreviousMessages(Long roomId);

    @Transactional
    void saveMessage(MessageDto messageDto);

    Long createChatRoom(ChatDto.Post request);

    Page<ChatDslDto> findAllChat(Pageable pageable);

    ChatPageDto findAllSearchByPaging(Pageable pageable);

    ChatPageDto findAllChatByConditionByPaging(SearchChat searchChat, Pageable pageable);

    void deleteById(Long id);

    List<ChatDto.MessageResponse> getPreviousMessagesFromRedis(Long roomId);

    ChatRoom getChatRoomById(Long roomId);

}
