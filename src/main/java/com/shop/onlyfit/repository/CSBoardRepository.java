package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.dto.CustomServiceBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CSBoardRepository extends JpaRepository<CustomServiceBoard, Integer> {

    @Query("select cb from CustomServiceBoard cb order by cb.id desc ")
    Page<CustomServiceBoard> findByTitleContaining(String title, Pageable pageable);

    @Query("select cb from CustomServiceBoard cb where cb.boardType = 'NOTICE' order by cb.id desc ")
    List<CustomServiceBoard> loadNoticeBoard();
}
