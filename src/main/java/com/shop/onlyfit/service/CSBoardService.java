package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.CustomServiceReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public interface CSBoardService {
    Page<CustomServiceBoard> findByTitle(String st, Pageable pageable);

    List<CustomServiceBoard> loadNoticeBoards();

    ArrayList<Integer> makePageNumbers(Page<CustomServiceBoard> boards);

    CustomServiceBoard findCSboardByid(int id);

    void writeBoard(CustomServiceBoard serviceBoard);

    boolean updateCsBoard(CustomServiceBoard board);

    void removeBoard(int id);

    CustomServiceReply saveReply(CustomServiceReply customServiceReply, int id);

    void deleteReply(int id);

}
