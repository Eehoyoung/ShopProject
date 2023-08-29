package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.service.ChatServiceImpl;
import com.shop.onlyfit.service.ItemServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;
    private final ItemServiceImpl itemService;

    @PutMapping("/admin/changepassword_ok")
    public String changeAdminPasswordPage(Principal principal, @RequestParam("password") String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User findUser = userService.findUserByLoginId(principal.getName());

        findUser.setPassword(newPassword);
        userService.changePassword(findUser.getId(), passwordEncoder.encode(newPassword));

        return "관리자 비밀번호 수정 완료";
    }

    @PatchMapping("/admin/itemList/onsale")
    public String itemStatusOnSalePage(@RequestBody List<Map<String, String>> allData) {
        for (Map<String, String> temp : allData) {
            itemService.changeItemStatusOnSale(temp.get("itemIdx"), temp.get("itemColor"));
        }
        return "상품 상태 판매로 변경완료";
    }

    @PatchMapping("/admin/itemList/soldout")
    public String itemStatusSoldOutPage(@RequestBody List<Map<String, String>> allData) {
        for (Map<String, String> temp : allData) {
            itemService.changeItemStatusSoldOut(temp.get("itemIdx"), temp.get("itemColor"));
        }
        return "상품 상태 품절로 변경완료";
    }

    @DeleteMapping("/admin/itemList/remove")
    public String itemdeletePage(@RequestBody List<Map<String, String>> allData) {
        for (Map<String, String> temp : allData) {
            itemService.deleteItemById(temp.get("itemIdx"), temp.get("itemColor"));
        }
        return "상품 삭제 완료";
    }

    @DeleteMapping("/admin/chatList/{id}")
    public String deleteChat(@PathVariable Long id) {
        chatService.deleteById(id);

        return "채팅 삭제 완료";
    }

    @DeleteMapping("/admin/chatList")
    public String deleteChatChecked(@RequestParam(value = "idList", required = false) List<Long> idList) {

        for (Long aLong : idList) {
            chatService.deleteById(aLong);
        }
        return "선택된 채팅 삭제 완료";
    }

    @DeleteMapping("/admin/userList/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);

        return "회원 삭제 완료";
    }

    @DeleteMapping("/admin/userList")
    public String deleteChecked(@RequestParam(value = "idList", required = false) List<Long> idList) {

        for (Long aLong : idList) {
            userService.deleteById(aLong);
        }
        return "선택된 회원 삭제 완료";
    }
}
