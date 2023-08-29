package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.*;
import com.shop.onlyfit.dto.ChatDslDto;
import com.shop.onlyfit.dto.ChatPageDto;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.OrderPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.dto.user.UserDto;
import com.shop.onlyfit.dto.user.UserPageDto;
import com.shop.onlyfit.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminServiceImpl adminService;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final OrderServiceImpl orderService;
    private final ChatServiceImpl chatService;

    @GetMapping("/admin/changepassword")
    public String adminChangePassword() {
        return "admin/admin_changePassword";
    }

    @GetMapping("/admin/main")
    public String getUserMainPage(Model model, @PageableDefault(size = 4) Pageable pageable) {
        Page<User> userBoards = adminService.findAllUserByOrderByCreateAt(pageable);
        Page<ItemDto> itemBoards = itemService.findAllItem(pageable);
        Page<OrderDto> orderBoards = orderService.findAllOrder(pageable);
        Page<ChatDslDto> chatBoards = chatService.findAllChat(pageable);
        int allVisitCount = userService.getVisitCount();

        model.addAttribute("userList", userBoards);
        model.addAttribute("itemList", itemBoards);
        model.addAttribute("orderList", orderBoards);
        model.addAttribute("numVisitors", allVisitCount);
        model.addAttribute("chatList", chatBoards);

        return "admin/admin_main";
    }

    static void getItemBoard(Model model, SearchItem searchItem, ItemPageDto itemPageDto) {
        Page<ItemDto> itemBoards = itemPageDto.getItemPage();
        int homeStartPage = itemPageDto.getHomeStartPage();
        int homeEndPage = itemPageDto.getHomeEndPage();

        model.addAttribute("productList", itemBoards);
        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);

        model.addAttribute("saleStatus", searchItem.getSalestatus());
        model.addAttribute("firstCategory", searchItem.getCmode());
        model.addAttribute("itemName", searchItem.getItem_name());
    }

    static void getOrderboard(Model model, SearchOrder searchOrder, OrderPageDto orderPageDto) {
        Page<OrderDto> orderBoards = orderPageDto.getOrderBoards();
        int homeStartPage = orderPageDto.getHomeStartPage();
        int homeEndPage = orderPageDto.getHomeEndPage();

        model.addAttribute("orderList", orderBoards);
        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);

        model.addAttribute("firstDate", searchOrder.getFirstdate());
        model.addAttribute("lastDate", searchOrder.getLastdate());
        model.addAttribute("oMode", searchOrder.getOmode());
        model.addAttribute("sMode", "buyer");
        model.addAttribute("sInput", searchOrder.getSinput());
        model.addAttribute("oModeStatus", searchOrder.getOmode());
    }

    @GetMapping("/admin/userList")
    public String pageList(Model model, @PageableDefault(size = 4) Pageable pageable, SearchUser searchUser) {
        UserPageDto userPageDto = new UserPageDto();

        if (searchUser.getSearchKeyword() == null) {
            userPageDto = userService.findAllUserByPaging(pageable);
        } else {
            userPageDto = userService.findAllUserByConditionByPaging(searchUser, pageable);
        }

        int homeStartPage = userPageDto.getStartPage();
        int homeEndPage = userPageDto.getEndPage();
        Page<UserDto> userBoards = userPageDto.getUserPage();

        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);
        model.addAttribute("userList", userBoards);
        model.addAttribute("searchCondition", searchUser.getSearchCondition());
        model.addAttribute("searchKeyword", searchUser.getSearchKeyword());

        return "admin/admin_userlist";
    }

    @GetMapping("/admin/chatList")
    public String chatList(Model model, @PageableDefault(size = 4) Pageable pageable, SearchChat searchChat) {
        ChatPageDto chatPageDto = new ChatPageDto();

        if (searchChat.getSearchKeyword() == null) {
            chatPageDto = chatService.findAllSearchByPaging(pageable);
        } else {
            chatPageDto = chatService.findAllChatByConditionByPaging(searchChat, pageable);
        }

        int homeStartPage = chatPageDto.getStartPage();
        int homeEndPage = chatPageDto.getEndPage();
        Page<ChatDslDto> chatBoards = chatPageDto.getChatBoards();

        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);
        model.addAttribute("chatList", chatBoards);
        model.addAttribute("searchCondition", searchChat.getSearchCondition());
        model.addAttribute("searchKeyword", searchChat.getSearchKeyword());

        return "admin/admin_chatlist";
    }

    @GetMapping("/admin/userList/user/{id}")
    public String pageUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));

        return "admin/admin_user";
    }

    @GetMapping("/admin/itemList")
    public String itemListPage(Model model, @PageableDefault(size = 5) Pageable pageable, SearchItem searchItem) {
        ItemPageDto itemPageDto = new ItemPageDto();
        if (searchItem.getItem_name() == null) {
            itemPageDto = itemService.findAllItemByPaging(pageable);
        } else {
            itemPageDto = itemService.findAllItemByConditionByPaging(searchItem, pageable);
        }
        getItemBoard(model, searchItem, itemPageDto);

        return "admin/admin_Goodslist";
    }

    @GetMapping("/admin/orderList")
    public String getOrderPage(Model model, @PageableDefault(size = 4) Pageable pageable, SearchOrder searchOrder) {

        OrderPageDto orderPageDto = new OrderPageDto();

        if (StringUtils.isEmpty(searchOrder.getFirstdate()) && StringUtils.isEmpty(searchOrder.getLastdate()) && StringUtils.isEmpty(searchOrder.getSinput())) {
            orderPageDto = orderService.findAllOrderByPaging(pageable);
        } else {
            orderPageDto = orderService.findAllOrderByConditionByPaging(searchOrder, pageable);
        }

        getOrderboard(model, searchOrder, orderPageDto);

        return "admin/admin_order";

    }
}