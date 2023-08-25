package com.shop.onlyfit.service;

import com.shop.onlyfit.dto.ChatDto;
import com.shop.onlyfit.dto.MessageDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatService {

    @Transactional
    List<ChatDto.MessageResponse> getPreviousMessages(Long roomId);

    @Transactional
    void saveMessage(MessageDto messageDto);

    Long createChatRoom(ChatDto.Post request);
}
