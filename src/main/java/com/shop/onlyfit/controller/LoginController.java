package com.shop.onlyfit.controller;

import com.shop.onlyfit.auth.jwt.JwtTokenProvider;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MarketInfoDto;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.service.KakaoAuthService;
import com.shop.onlyfit.service.MileageServiceImpl;
import com.shop.onlyfit.service.TokenService;
import com.shop.onlyfit.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserServiceImpl userService;
    private final MileageServiceImpl mileageService;
    private final KakaoAuthService kakaoAuthService;
    private final TokenService tokenService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping("/main/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "/main/index");
            return null;
        }
        return "/main/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            tokenService.logoutToken(request, response);
        }
        return "redirect:/main/index";
    }

    @GetMapping("main/join")
    public String join(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "/main/index");
            return null;
        }
        return "main/join";
    }

    @PostMapping("main/join")
    public String join(UserInfoDto userInfoDto) {
        Long userId = userService.joinUser(userInfoDto);
        mileageService.joinMileage(userId);

        return "redirect:/main/login";
    }

    @GetMapping("main/join/seller")
    public String joinSeller(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/main/login";
        }
        return "main/join_seller";
    }

    @PostMapping("main/join/seller")
    public String joinSeller(@AuthenticationPrincipal UserDetails userDetails, MarketInfoDto marketInfoDto) {
        String userId = userDetails.getUsername();
        userService.joinSeller(userId, marketInfoDto);
        userService.changeUserGradeToSeller(marketInfoDto.getUser().getId());
        return "redirect:/main/index";
    }

    @GetMapping("/defaultUrl")
    public String defaultUrl(HttpServletRequest req) {
        req.getHeader("Referer");
        String session;

        session = "http://localhost:8080/main/index";
        req.getSession().setAttribute("prevPage", session);

        return "redirect:/main/index";
    }

    @ApiOperation("kakao Login")
    @GetMapping("/auth/kakao/login_proc")
    public String kakaoLogin(@RequestParam String code,
                             HttpServletResponse response) {
        String redirectUri = "http://localhost:8080/auth/kakao/login_proc";
        String clientId = "5aa9b77cb80c98f7948b0254eacdf119";
        try {
            String accessToken = kakaoAuthService.getAccessToken(code, redirectUri, clientId);
            User authenticatedUser = kakaoAuthService.getAuthenticatedUser(accessToken);
            String JwtToken = tokenProvider.createToken(authenticatedUser);

            Cookie jwtCookie = tokenService.createJwtCookie(authenticatedUser, JwtToken);

            response.addCookie(jwtCookie);

            if (authenticatedUser.getVisitCount() >= 2) {
                return "redirect:/main/index";
            }

            return "redirect:/main/index";
        } catch (Exception e) {
            return "main/restrict";
        }
    }
}
