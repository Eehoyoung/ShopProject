package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select cr.roomId from ChatRoom cr where cr.sender.id = :userId")
    Long findRoomIdByUserId(@Param("userId") Long userId);
}