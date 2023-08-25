package com.shop.onlyfit.controller;

import com.shop.onlyfit.dto.ChatDto;
import com.shop.onlyfit.dto.MessageDto;
import com.shop.onlyfit.dto.MultiResponseDto;
import com.shop.onlyfit.dto.PageInfo;
import com.shop.onlyfit.service.ChatServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceImpl chatRoomService;
    private final UserServiceImpl userService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<Long> createChatRoom(@RequestBody ChatDto.Post request) {
        Long roomId = chatRoomService.createChatRoom(request);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/chat/room/{roomId}")
    public String chatRoom(@PathVariable String roomId, Model model, Principal principal) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", userService.getUserId(principal.getName()));
        model.addAttribute("username", userService.getUserName(principal.getName()));
        return "/main/chat_room";
    }

    // 이전 채팅 기록을 가져오는 API
    @GetMapping("/room/{roomId}/messages")
    @ResponseBody
    public MultiResponseDto<ChatDto.MessageResponse> getPreviousMessages(@PathVariable Long roomId) {
        List<ChatDto.MessageResponse> messages = chatRoomService.getPreviousMessages(roomId);

        boolean success = true;

        PageInfo pageInfo = new PageInfo(1, messages.size(), messages.size(), 1);

        MultiResponseDto<ChatDto.MessageResponse> response = new MultiResponseDto<>(success, messages, pageInfo);

        response.setSuccess(!messages.isEmpty());

        return response;
    }

    // 클라이언트로부터 메시지 받아서 다시 브로드캐스트하는 메소드
    @MessageMapping("/chat/{roomId}")
    @ResponseBody
    public void broadcastMessage(@DestinationVariable("roomId") Long roomId, @RequestBody MessageDto messageDto) {
        chatRoomService.saveMessage(messageDto);  // 메시지를 DB에 저장

        String nickName = userService.getUserNameByUserCode(messageDto.getSenderId());
        messageDto.setNickName(nickName);
        messageDto.setSendTime(LocalDateTime.now());

        messagingTemplate.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);  // 저장된 메시지를 다른 클라이언트에게 전송
    }

}
