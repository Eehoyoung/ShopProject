package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.CustomServiceBoard;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.service.CSBoardServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CSBoardController {
    private final CSBoardServiceImpl csBoardService;
    private final UserServiceImpl userService;

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
}
