package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.CustomServiceReply;
import com.shop.onlyfit.repository.CSBoardRepository;
import com.shop.onlyfit.repository.CSReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CSBoardServiceImpl implements CSBoardService {

    private final CSBoardRepository csBoardRepository;
    private final CSReplyRepository csReplyRepository;

    @Autowired
    public CSBoardServiceImpl(CSBoardRepository csBoardRepository, CSReplyRepository csReplyRepository) {
        this.csBoardRepository = csBoardRepository;
        this.csReplyRepository = csReplyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomServiceBoard> findByTitle(String st, Pageable pageable) {
        System.out.println("서비스단  " + st);
        return csBoardRepository.findByTitleContaining(st, pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CustomServiceBoard> loadNoticeBoards() {
        return csBoardRepository.loadNoticeBoard();
    }

    @Override
    @Transactional
    public ArrayList<Integer> makePageNumbers(Page<CustomServiceBoard> boards) {
        int nowPage = boards.getPageable().getPageNumber();
        int startPage = Math.max(nowPage - 2, 0); // 두 인트값 중에 큰 값을 반환 한다.
        int endPage = Math.min(nowPage + 2, boards.getTotalPages() - 1);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = startPage; i <= endPage; i++) {
            list.add(i);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomServiceBoard findCSboardByid(int id) {
        return csBoardRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 게시글를 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void writeBoard(CustomServiceBoard serviceBoard) {
        csBoardRepository.save(serviceBoard);
    }

    @Override
    @Transactional
    public boolean updateCsBoard(CustomServiceBoard board) {
        CustomServiceBoard oldBoard = csBoardRepository.findById(board.getId()).orElseThrow(
                () -> new RuntimeException("해당 게시글를 찾을 수 없습니다.")
        );
        oldBoard.setTitle(board.getTitle());
        oldBoard.setContent(board.getContent());
        oldBoard.setSecret(board.getSecret());
        return true;
    }

    @Override
    @Transactional
    public void removeBoard(int id) {
        csBoardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CustomServiceReply saveReply(CustomServiceReply customServiceReply, int id) {
        CustomServiceBoard targetBoard = csBoardRepository.findById(id).orElseThrow(
                () -> new RuntimeException("해당 게시글를 찾을 수 없습니다.")
        );
        customServiceReply.setCustomServiceBoard(targetBoard);
        csReplyRepository.save(customServiceReply);
        return customServiceReply;
    }

    @Override
    @Transactional
    public void deleteReply(int id) {
        csReplyRepository.deleteById(id);
    }
}
