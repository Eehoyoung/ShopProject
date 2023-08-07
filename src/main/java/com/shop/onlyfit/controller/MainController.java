package com.shop.onlyfit.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.shop.onlyfit.auth.jwt.JwtProperties;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.service.ItemServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class MainController {

    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    @Autowired
    public MainController(ItemServiceImpl itemService, UserServiceImpl userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        String jwtHeader = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtProperties.HEADER_STRING.equals(cookie.getName())) {
                    jwtHeader = cookie.getValue();
                }
            }
        }
        if (jwtHeader == null) {
            return null;
        }
        Long userId;
        userId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtHeader).getClaim("id").asLong();
        return userService.findByUserId(userId);
    }

    @GetMapping("/main/index")
    public String getMainPage(Model model, HttpServletRequest request) {

        User authenticatedUser = getAuthenticatedUser(request);

        if (authenticatedUser != null) {
            model.addAttribute("user", authenticatedUser);
        }
        List<Item> mainCarouselList = itemService.MainCarouselItemList();

        List<WeeklyBestDto> outerWeeklyBestItem = itemService.OuterWeeklyBestItem();
        List<WeeklyBestDto> bottomWeeklyBestItem = itemService.BottomWeeklyBestItem();
        List<WeeklyBestDto> shirtsWeeklyBestItem = itemService.ShirtsWeeklyBestItem();
        List<WeeklyBestDto> shoesWeeklyBestItem = itemService.ShoesWeeklyBestItem();
        List<WeeklyBestDto> sleeveTopWeeklyBestItem = itemService.SleeveTopWeeklyBestItem();
        List<WeeklyBestDto> topKnitWeeklyBestItem = itemService.TopKnitWeeklyBestItem();
        List<WeeklyBestDto> newArrivalItemList = itemService.NewArrivalItem();


        model.addAttribute("mainCarousel", mainCarouselList);
        model.addAttribute("outerList", outerWeeklyBestItem);
        model.addAttribute("topList", sleeveTopWeeklyBestItem);
        model.addAttribute("shirtList", shirtsWeeklyBestItem);
        model.addAttribute("knitList", topKnitWeeklyBestItem);
        model.addAttribute("bottomList", bottomWeeklyBestItem);
        model.addAttribute("shoesList", shoesWeeklyBestItem);
        model.addAttribute("newarrivals", newArrivalItemList);

        return "main/index";
    }

    @GetMapping("/main/restrict")
    public String getRestrictPage() {
        return "main/restrict";
    }

}
