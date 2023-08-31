package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.PublishMessage;
import com.shop.onlyfit.dto.ChatDto;
import com.shop.onlyfit.dto.MessageDto;
import com.shop.onlyfit.dto.MultiResponseDto;
import com.shop.onlyfit.dto.PageInfo;
import com.shop.onlyfit.service.ChatServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatServiceImpl chatRoomService;
    private final UserServiceImpl userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelTopic topic;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate<String, MessageDto> chatRedisTemplate;


    @ApiOperation("Create ChatRoom")
    @PostMapping("/room")
    public ResponseEntity<Long> createChatRoom(@RequestBody ChatDto.Post request) {
        Long roomId = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(roomId);
    }

    @ApiOperation("Get Messages In Redis")
    @GetMapping("/room/{roomId}/messages")
    public MultiResponseDto<ChatDto.MessageResponse> getPreviousMessages(@PathVariable Long roomId) {
        List<ChatDto.MessageResponse> messages = chatRoomService.getPreviousMessagesFromRedis(roomId);  // Redis에서 메시지를 가져옴

        boolean success = true;

        PageInfo pageInfo = new PageInfo(1, messages.size(), messages.size(), 1);

        MultiResponseDto<ChatDto.MessageResponse> response = new MultiResponseDto<>(success, messages, pageInfo);

        response.setSuccess(!messages.isEmpty());

        return response;
    }

    @ApiOperation("Send Message Pub/Sub patter")
    @MessageMapping("/chat/{roomId}")
    public void broadcastMessage(@DestinationVariable("roomId") Long roomId, @RequestBody MessageDto messageDto) {
        PublishMessage publishMessage =
                new PublishMessage(messageDto.getRoomId(), messageDto.getSenderId(), messageDto.getContent(), LocalDateTime.now());

        chatRedisTemplate.convertAndSend(topic.getTopic(), publishMessage);

        String nickName = userService.getUserNameByUserCode(messageDto.getSenderId());

        messageDto.setNickName(nickName);

        messageDto.setSendTime(LocalDateTime.now());

        String redisKey = "chatroom:" + roomId;

        // Redis에 데이터 저장 (List 사용)
        chatRedisTemplate.opsForList().rightPush(redisKey, messageDto);

        // TTL 설정 (72 hours)
        chatRedisTemplate.expire(redisKey, Duration.ofHours(72));

        messagingTemplate.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
    }
}
