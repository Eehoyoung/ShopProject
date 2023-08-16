package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.CustomServiceReply;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.CSBoardType;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.CsPageDto;
import com.shop.onlyfit.dto.CustomServiceBoardDto;
import com.shop.onlyfit.dto.ResponseDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.service.CSBoardServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
public class CSBoardController {
    private final CSBoardServiceImpl csBoardService;
    private final UserServiceImpl userService;

    @Autowired
    public CSBoardController(CSBoardServiceImpl csBoardService, UserServiceImpl userService) {
        this.csBoardService = csBoardService;
        this.userService = userService;
    }

    @GetMapping("/main/cs")
    public String intoCustomerService(
            @PageableDefault(size = 10, direction = Sort.Direction.DESC, page = 0, sort = "id") Pageable pageable, @RequestParam(required = false, defaultValue = "") String q,
            Model model) {
        String st = (q.isEmpty()) ? "" : q;
        Page<CustomServiceBoard> boards = csBoardService.findByTitle(st, pageable);
        List<CustomServiceBoard> noticeBoards = csBoardService.loadNoticeBoards();
        ArrayList<Integer> list;
        list = csBoardService.makePageNumbers(boards);
        model.addAttribute("notices", noticeBoards);
        model.addAttribute("boards", boards);
        System.out.println("보드" + boards);
        model.addAttribute("pageNums", list);
        return "/main/custom_service";
    }

    @GetMapping("/main/cs-write")
    public String writeCustomerBoard() {
        return "/main/custom_board_write";
    }

    @GetMapping("/main/cs-update/{id}")
    public String updateCustomerBoard(@PathVariable int id, Model model) {
        CustomServiceBoard csBoard = csBoardService.findCSboardByid(id);
        model.addAttribute("board", csBoard);
        return "/main/custom_board_update";
    }

    @GetMapping("/main/cs/detail/{id}")
    public String showDetail(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        CustomServiceBoard csBoard = csBoardService.findCSboardByid(id);
        if (csBoard.getSecret() == 1) {
            if (findUser.getUserGrade() != UserGrade.ADMIN && !Objects.equals(csBoard.getUser().getId(), findUser.getId())) {
                return "redirect:/main/cs";
            }
        }
        model.addAttribute("board", csBoard);
        return "/main/customer_board_detail";
    }

    @PostMapping("/main/cs-write")
    @ResponseBody
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
    @ResponseBody
    public ResponseDto<String> updateBoard(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CustomServiceBoard board) {
        System.out.println(board);
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
    @ResponseBody
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
    @ResponseBody
    public ResponseDto<CustomServiceReply> writeReply(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody CustomServiceReply customServiceReply,
                                                      @RequestParam(value = "boardId") int id) {
        System.out.println(customServiceReply);
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!findUser.getUserGrade().equals(UserGrade.ADMIN)) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), null);
        }

        CustomServiceReply csReply = csBoardService.saveReply(customServiceReply, id);
        System.out.println(csReply);
        return new ResponseDto<>(HttpStatus.OK.value(), customServiceReply);
    }

    @DeleteMapping("/main/cs-delete/reply")
    @ResponseBody
    public ResponseDto<Integer> deleteReply(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "replyId") int id) {
        User findUser = userService.findUserByLoginId(userDetails.getUsername());
        if (!findUser.getUserGrade().equals(UserGrade.ADMIN)) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), null);
        }
        csBoardService.deleteReply(id);
        return new ResponseDto<>(HttpStatus.OK.value(), id);
    }

}
