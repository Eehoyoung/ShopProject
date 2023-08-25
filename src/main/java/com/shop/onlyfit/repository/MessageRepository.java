package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select c from ChatMessage c where c.chatRoom.roomId = :roomId")
    List<ChatMessage> findByChatRoomid(@Param("roomId") Long roomId);

}