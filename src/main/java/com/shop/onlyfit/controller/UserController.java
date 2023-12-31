package com.shop.onlyfit.controller;

import com.shop.onlyfit.auth.jwt.UnConnected;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.MyPageOrderStatusDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.service.KakaoAuthService;
import com.shop.onlyfit.service.OrderServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final KakaoAuthService kakaoAuthService;

    @GetMapping("main/mypage")
    public String getMyPage(Principal principal, Model model) {

        String loginId = principal.getName();

        MyPageDto myPageDto = userService.showMySimpleInfo(loginId);
        MyPageOrderStatusDto myPageOrderStatusDto = orderService.showOrderStatus(loginId);

        model.addAttribute("user", myPageDto);
        model.addAttribute("orderStatus", myPageOrderStatusDto);

        return "main/mypage";
    }

    @GetMapping("/main/profile")
    public String editDataPage(Principal principal, Model model, @ModelAttribute("user") User user) {
        String loginId = principal.getName();
        ProfileDto myProfileDto = userService.showProfileData(loginId);

        model.addAttribute("user", myProfileDto);

        return "main/editdata";
    }

    @PutMapping("/update")
    public String editDataPage(Principal principal, @ModelAttribute("user") ProfileDto profileDto) {

        userService.updateProfile(principal.getName(), profileDto);

        return "redirect:/main/mypage";
    }

    @ApiOperation("User Withdrawal")
    @ResponseBody
    @DeleteMapping("/main/withdrawal")
    public String withdrawalMember(HttpServletRequest request, HttpServletResponse response, Principal principal, @RequestParam(value = "user_pw") String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String loginId = principal.getName();
        User findUser = userService.findByLoginId(loginId);
        if (findUser.getName().contains("kakao_")) {
            Long tId = Long.valueOf(findUser.getLoginId());
            UnConnected unConnected = kakaoAuthService.kakaoUnconnected(tId);
            if (Objects.equals(unConnected.getId(), tId)) {
                userService.deleteUserByLoginId(String.valueOf(unConnected.getId()));
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                }
                return "정상적으로 회원탈퇴 되었습니다.";
            } else {
                return "회원탈퇴가 처리되지 못했습니다.";
            }
        }
        boolean result = passwordEncoder.matches(password, findUser.getPassword());

        if (result) {
            userService.deleteUserByLoginId(loginId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
            return "정상적으로 회원탈퇴되었습니다.";
        } else {
            return "비밀번호가 올바르지 않습니다";
        }
    }
}
