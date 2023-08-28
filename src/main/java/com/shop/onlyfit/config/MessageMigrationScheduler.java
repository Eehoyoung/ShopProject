package com.shop.onlyfit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.onlyfit.domain.chat.ChatMessage;
import com.shop.onlyfit.dto.MessageDto;
import com.shop.onlyfit.repository.MessageRepository;
import com.shop.onlyfit.service.ChatServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MessageMigrationScheduler {

    //    private final StringRedisTemplate chatRedisTemplate;
    private final MessageRepository messageRepository;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    @Qualifier("chatRedisTemplate")
    private RedisTemplate<String, MessageDto> chatRedisTemplate;

    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void migrateMessages() {
        Cursor<byte[]> cursor = Objects
                .requireNonNull(
                        chatRedisTemplate.getConnectionFactory()
                )
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match("chatroom:*")
                        .count(1000)
                        .build());

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            String valueJson = String.valueOf(chatRedisTemplate.opsForValue().get(key)); // this part error

            if (valueJson != null) {
                try {
                    // Convert JSON string to MessageDto object.
                    MessageDto messageDto = objectMapper.readValue(valueJson, MessageDto.class);

                    // Convert MessageDto to ChatMessage entity.
                    ChatMessage message = ChatMessage.builder()
                            .content(messageDto.getContent())
                            .sendTime(messageDto.getSendTime())
                            // Assuming you have methods for getting User and ChatRoom entities by their IDs.
                            .sender(userService.getUserById(messageDto.getSenderId()))
                            .chatRoom(chatService.getChatRoomById(messageDto.getRoomId()))
                            .build();

                    messageRepository.save(message);

                    chatRedisTemplate.delete(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
