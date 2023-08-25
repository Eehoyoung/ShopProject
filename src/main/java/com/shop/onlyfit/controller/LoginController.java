package com.shop.onlyfit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shop.onlyfit.auth.jwt.JwtProperties;
import com.shop.onlyfit.auth.jwt.JwtTokenProvider;
import com.shop.onlyfit.auth.jwt.JwtTokenUtil;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.FindIdRep;
import com.shop.onlyfit.dto.FindIdReq;
import com.shop.onlyfit.dto.Login;
import com.shop.onlyfit.dto.MarketInfoDto;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.service.AuthService;
import com.shop.onlyfit.service.MileageServiceImpl;
import com.shop.onlyfit.service.RedisService;
import com.shop.onlyfit.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserServiceImpl userService;
    private final MileageServiceImpl mileageService;
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisService redisService;
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
            logoutToken(request, response);
        }
        return "redirect:/main/index";
    }

    public void logoutToken(HttpServletRequest request, HttpServletResponse response) {

        String token = tokenProvider.getTokenFromRequest(request);

        Base64.Decoder decoder = Base64.getDecoder();
        final String[] splitJwt = Objects.requireNonNull(token).split("\\.");
        final String payloadStr = new String(decoder.decode(splitJwt[1].getBytes()));

        Long userId = getUserIdFromToken(payloadStr);
        Date expirationDate = getDateExpFromToken(payloadStr);
        System.out.println("아이디  " + userId + "시간:  " + expirationDate);
        redisService.saveToken(token, userId, expirationDate);
        clearCookie(JwtProperties.HEADER_STRING, request, response);
    }

    public Long getUserIdFromToken(String payloadStr) {
        JsonObject jsonObject = new Gson().fromJson(payloadStr, JsonObject.class);
        return jsonObject.get("id").getAsLong();
    }

    public Date getDateExpFromToken(String payloadStr) {
        JsonObject jsonObject = new Gson().fromJson(payloadStr, JsonObject.class);
        long exp = jsonObject.get("exp").getAsLong();
        return new Date(exp * 1000);
    }

    public void clearCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0); // 쿠키의 만료시간을 0으로 설정하여 삭제
                    cookie.setHttpOnly(true); // HttpOnly 설정
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
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

    @ResponseBody
    @PostMapping("/main/join/doublecheck")
    public String idDoubleCheckPage(@RequestParam(value = "userID") String userId) {
        if (userService.checkId(userId)) {
            return "사용할 수 없는 아이디입니다.";
        } else {
            return "사용할 수 있는 아이디입니다.";
        }
    }

    @ResponseBody
    @PostMapping("/main/join/seller/doublecheck")
    public String MarketNameDoubleCheckPage(@RequestParam(value = "name") String name) {
        if (userService.checkName(name)) {
            return "사용할 수 없는 매장명 입니다.";
        } else {
            return "사용할 수 있는 매장명 입니다.";
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
            String JwtToken = tokenProvider.createToken(authenticatedUser);

            Cookie jwtCookie = createJwtCookie(authenticatedUser, JwtToken);

            response.addCookie(jwtCookie);

            if (authenticatedUser.getVisitCount() >= 2) {
                return "redirect:/main/index";
            }

            return "redirect:/main/profile";
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
        jwtToken = JwtProperties.TOKEN_PREFIX.trim() + jwtToken;
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

    @PostMapping("/findId")
    @ResponseBody
    public ResponseEntity<?> findId(@RequestBody FindIdReq findIdReq) {
        String loginId = userService.findLoginId(findIdReq.getName(), findIdReq.getPhoneNum());
        if (loginId != null) {
            return ResponseEntity.ok(new FindIdRep(loginId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/password-reset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> passwordReset(@RequestBody Map<String, String> requestData) {
        String userId = requestData.get("userId");
        String name = requestData.get("name");
        String phoneNum = requestData.get("phoneNum");
        String newPassword = requestData.get("newPassword");

        boolean success = userService.resetPassword(userId, name, phoneNum, newPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);

        return ResponseEntity.ok(response);
    }
}
