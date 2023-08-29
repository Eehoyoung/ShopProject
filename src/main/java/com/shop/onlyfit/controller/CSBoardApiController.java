package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.CustomServiceReply;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.CSBoardType;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.ResponseDto;
import com.shop.onlyfit.service.CSBoardServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CSBoardApiController {

    private final UserServiceImpl userService;
    private final CSBoardServiceImpl csBoardService;

    @PostMapping("/main/cs-write")
    public ResponseDto<Integer> writeBoard(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CustomServiceBoard serviceBoard) {
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        serviceBoard.setUser(findUser);
        if (findUser.getUserGrade().equals(UserGrade.ADMIN)) {
            serviceBoard.setBoardType(CSBoardType.NOTICE);
        } else {
            serviceBoard.setBoardType(CSBoardType.NORMAL);
        }
        csBoardService.writeBoard(serviceBoard);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }

    @PutMapping("/main/cs-update")
    public ResponseDto<String> updateBoard(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CustomServiceBoard board) {
        CustomServiceBoard tempBoard = csBoardService.findCSboardByid(board.getId());
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!Objects.equals(tempBoard.getUser().getId(), findUser.getId())) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), "NO");
        }

        if (csBoardService.updateCsBoard(board)) {
            return new ResponseDto<>(HttpStatus.OK.value(), "OK");
        } else {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), "NO");
        }
    }

    @DeleteMapping("/main/cs-delete")
    public ResponseDto<String> deleteBoard(@RequestParam(value = "boardId") int id, @AuthenticationPrincipal UserDetails userDetails) {
        CustomServiceBoard tempBoard = csBoardService.findCSboardByid(id);
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!Objects.equals(tempBoard.getUser().getId(), findUser.getId())) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), "NO");
        }
        csBoardService.removeBoard(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "OK");
    }

    @PostMapping("/main/cs-write/reply")
    public ResponseDto<CustomServiceReply> writeReply(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody CustomServiceReply customServiceReply,
                                                      @RequestParam(value = "boardId") int id) {
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!findUser.getUserGrade().equals(UserGrade.ADMIN)) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), null);
        }

        CustomServiceReply csReply = csBoardService.saveReply(customServiceReply, id);
        return new ResponseDto<>(HttpStatus.OK.value(), customServiceReply);
    }

    @DeleteMapping("/main/cs-delete/reply")
    public ResponseDto<Integer> deleteReply(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "replyId") int id) {
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!findUser.getUserGrade().equals(UserGrade.ADMIN)) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), null);
        }
        csBoardService.deleteReply(id);
        return new ResponseDto<>(HttpStatus.OK.value(), id);
    }

}
