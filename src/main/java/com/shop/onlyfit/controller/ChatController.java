package com.shop.onlyfit.controller;

import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final UserServiceImpl userService;

    @GetMapping("/chat/room/{roomId}")
    public String chatRoom(@PathVariable String roomId, Model model, Principal principal) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", userService.getUserId(principal.getName()));
        model.addAttribute("username", userService.getUserName(principal.getName()));
        return "/main/chat_room";
    }
}
