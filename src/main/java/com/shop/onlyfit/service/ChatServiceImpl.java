package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchChat;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.chat.ChatMessage;
import com.shop.onlyfit.domain.chat.ChatRoom;
import com.shop.onlyfit.dto.*;
import com.shop.onlyfit.repository.MessageRepository;
import com.shop.onlyfit.repository.RoomRepository;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository chatMessageRepository;

    @Autowired
    @Qualifier("chatRedisTemplate")
    private RedisTemplate<String, MessageDto> chatRedisTemplate;

    @Transactional
    @Override
    public List<ChatDto.MessageResponse> getPreviousMessages(Long roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomid(roomId);
        return chatMessages.stream()
                .map(chatMessage -> ChatDto.MessageResponse.builder()
                        .messageId(chatMessage.getMessageId())
                        .sender(ChatUserDto.ResponseOnlyUserName.builder()
                                .userId(chatMessage.getSender().getId())
                                .nickName(chatMessage.getSender().getName())
                                .build())
                        .content(chatMessage.getContent())
                        .sendTime(chatMessage.getSendTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void saveMessage(MessageDto messageDto) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(User.builder().id(messageDto.getSenderId()).build())
                .chatRoom(ChatRoom.builder().roomId(messageDto.getRoomId()).build())
                .content(messageDto.getContent())
                .sendTime(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);
    }

    @Override
    @Transactional
    public Long createChatRoom(ChatDto.Post request) {
        Long roomId = roomRepository.findRoomIdByUserId(request.getUserId());
        if (roomId != null) {
            return roomId;
        }
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")))
                .receiver(userRepository.findAdmin().orElseThrow(() -> new IllegalStateException("Admin user not found")))
                .build();

        roomRepository.save(chatRoom);

        return chatRoom.getRoomId();
    }

    @Override
    public Page<ChatDslDto> findAllChat(Pageable pageable) {
        return roomRepository.searchAllChat(pageable);
    }

    @Override
    public ChatPageDto findAllSearchByPaging(Pageable pageable) {
        ChatPageDto chatPageDto = new ChatPageDto();
        Page<ChatDslDto> chatBoards = roomRepository.searchAllChat(pageable);
        int startPage = Math.max(1, chatBoards.getPageable().getPageNumber());
        int endPage = Math.min(chatBoards.getTotalPages(), chatBoards.getPageable().getPageNumber() + 5);
        chatPageDto.setChatBoards(chatBoards);
        chatPageDto.setStartPage(startPage);
        chatPageDto.setEndPage(endPage);
        return chatPageDto;
    }

    @Override
    public ChatPageDto findAllChatByConditionByPaging(SearchChat searchChat, Pageable pageable) {
        ChatPageDto chatPageDto = new ChatPageDto();
        Page<ChatDslDto> chatBoards = roomRepository.searchByConditionAllChat(searchChat, pageable);
        int startPage = Math.max(1, chatBoards.getPageable().getPageNumber() - 2);
        int endPage = Math.min(chatBoards.getTotalPages(), chatBoards.getPageable().getPageNumber() + 4);
        chatPageDto.setChatBoards(chatBoards);
        chatPageDto.setStartPage(startPage);
        chatPageDto.setEndPage(endPage);
        return chatPageDto;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        chatMessageRepository.deleteByChatRoom_RoomId(id);
        roomRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatDto.MessageResponse> getPreviousMessagesFromRedis(Long roomId) {
        List<MessageDto> messageDtoList = chatRedisTemplate.opsForList().range("chatroom:" + roomId, 0, -1);

        return IntStream.range(0, Objects.requireNonNull(messageDtoList).size())
                .mapToObj(index -> {
                    MessageDto messageDto = messageDtoList.get(index);
                    return ChatDto.MessageResponse.builder()
                            .messageId(index) // Use the index as the messageId
                            .sender(ChatUserDto.ResponseOnlyUserName.builder()
                                    .userId(messageDto.getSenderId())
                                    .nickName(messageDto.getNickName())
                                    .build())
                            .content(messageDto.getContent())
                            .sendTime(messageDto.getSendTime())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Override
    public ChatRoom getChatRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                () -> new RuntimeException("채팅방을 찾을 수 없습니다.")
        );
    }
}

