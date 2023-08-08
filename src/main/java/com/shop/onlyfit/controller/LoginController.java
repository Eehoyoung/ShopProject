package com.shop.onlyfit.controller;

import com.shop.onlyfit.auth.jwt.JwtProperties;
import com.shop.onlyfit.auth.jwt.JwtTokenUtil;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.Login;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.service.AuthService;
import com.shop.onlyfit.service.MileageServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class LoginController {
    private final UserServiceImpl userService;
    private final MileageServiceImpl mileageService;
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public LoginController(UserServiceImpl userService, MileageServiceImpl mileageService, AuthService authService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.mileageService = mileageService;
        this.authService = authService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/main/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "/main/index");
            return null;
        }
        return "/main/login";
    }


//    @GetMapping("/logout")
//    public String logout() {
//        return "redirect:/main/index";
//
//    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String jwtHeader = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtProperties.HEADER_STRING.equals(cookie.getName())) {
                    jwtHeader = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }


        return ResponseEntity.ok("로그아웃을 성공했습니다.");
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

    @GetMapping("/defaultUrl")
    public String defaultUrl(HttpServletRequest req) {
        req.getHeader("Referer");
        String session;

        session = "http://localhost:8080/main/index";
        req.getSession().setAttribute("prevPage", session);

        return "redirect:/main/index";
    }

    @ResponseBody
    @PostMapping("/main/join/doublecheck")
    public String idDoubleCheckPage(@RequestParam(value = "userID") String userId) {
        if (userService.checkId(userId)) {
            return "사용할 수 없는 아이디입니다.";
        } else {
            return "사용할 수 있는 아이디입니다.";
        }
    }

    @GetMapping("/auth/kakao/login_proc")
    public String kakaoLogin(@RequestParam String code,
                             HttpServletResponse response) {
        String redirectUri = "http://localhost:8080/auth/kakao/login_proc";
        String clientId = "5aa9b77cb80c98f7948b0254eacdf119";
        try {
            String accessToken = authService.getAccessToken(code, redirectUri, clientId);
            User authenticatedUser = authService.getAuthenticatedUser(accessToken);
            String JwtToken = authService.createToken(authenticatedUser);

            Cookie jwtCookie = createJwtCookie(authenticatedUser, JwtToken);

            response.addCookie(jwtCookie);

            return "redirect:/main/index";
        } catch (Exception e) {
            return "main/restrict";
        }
    }


    @PostMapping("/main/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Login login, HttpServletResponse response) throws Exception {

        User authenticatedUser = userService.loadUserByLoginId(login.getLoginId()).orElseThrow(
                () -> new RuntimeException("해당 유저를 찾을 수 없습니다.")
        );

        final String JwtToken = jwtTokenUtil.generateToken(authenticatedUser);


        Cookie jwtCookie = createJwtCookie(authenticatedUser, JwtToken);

        response.addCookie(jwtCookie);

        return ResponseEntity.ok(jwtCookie);
    }

    private Cookie createJwtCookie(User authenticatedUser, String jwtToken) {
        UserDetails userDetails = this.userService.loadUserByUsername(authenticatedUser.getLoginId());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Cookie jwtCookie = new Cookie("Authorization", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(JwtProperties.EXPIRATION_TIME);
        jwtCookie.setPath("/");

        return jwtCookie;
    }

    @GetMapping("/login-user")
    @ResponseBody
    public String getUser(Principal principal) {
        return principal.getName();
    }
}
