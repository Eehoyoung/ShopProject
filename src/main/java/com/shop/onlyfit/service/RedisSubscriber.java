package com.shop.onlyfit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.onlyfit.domain.PublishMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String message) {
        try {
            PublishMessage publishMessage = objectMapper.readValue(message, PublishMessage.class);
            messagingTemplate.convertAndSend("/sub/chat/" + publishMessage.getRoomId(), publishMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}