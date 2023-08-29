package com.shop.onlyfit.config;

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

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MessageMigrationScheduler {

    private final MessageRepository messageRepository;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;

    @Autowired
    @Qualifier("chatRedisTemplate")
    private RedisTemplate<String, MessageDto> chatRedisTemplate;

    @Scheduled(fixedRate = 300000)
    public void migrateMessages() {
        // Redis의 모든 키를 스캔하기 위한 커서 생성. 여기에서는 chatroom:* 패턴에 매칭되는 키만 스캔함.
        Cursor<byte[]> cursor = Objects.requireNonNull(chatRedisTemplate.getConnectionFactory())
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match("chatroom:*")
                        .count(1000)
                        .build());

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            List<MessageDto> messageDtoList = chatRedisTemplate.opsForList().range(key, 0, -1);

            if (messageDtoList != null) {
                for (MessageDto messageDto : messageDtoList) {
                    try {
                        ChatMessage message = ChatMessage.builder()
                                .content(messageDto.getContent())
                                .sendTime(messageDto.getSendTime())
                                .sender(userService.getUserById(messageDto.getSenderId()))
                                .chatRoom(chatService.getChatRoomById(messageDto.getRoomId()))
                                .build();

                        messageRepository.save(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 기존 채팅방 재입장 시 기존 데이터를 불러오기 위해 임의 삭제가 아닌 TTL초과로 인한 삭제 방식으로 변경
//                chatRedisTemplate.delete(key);
            }
        }

        // 모든 작업을 마친 후 커서를 닫음. 이는 리소스 누수를 방지하기 위해 필요한 작업임.
        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}