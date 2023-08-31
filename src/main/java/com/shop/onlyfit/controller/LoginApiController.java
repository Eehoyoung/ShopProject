package com.shop.onlyfit.controller;

import com.shop.onlyfit.auth.jwt.JwtTokenUtil;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.FindIdRep;
import com.shop.onlyfit.dto.FindIdReq;
import com.shop.onlyfit.dto.Login;
import com.shop.onlyfit.service.TokenService;
import com.shop.onlyfit.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginApiController {

    private final UserServiceImpl userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    @ApiOperation("Login")
    @PostMapping("/main/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpServletResponse response) throws Exception {

        User authenticatedUser = userService.loadUserByLoginId(login.getLoginId()).orElseThrow(
                () -> new RuntimeException("해당 유저를 찾을 수 없습니다.")
        );

        final String JwtToken = jwtTokenUtil.generateToken(authenticatedUser);

        Cookie jwtCookie = tokenService.createJwtCookie(authenticatedUser, JwtToken);

        response.addCookie(jwtCookie);

        return ResponseEntity.ok(jwtCookie);
    }

    @ApiOperation("Find LoginId")
    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestBody FindIdReq findIdReq) {
        String loginId = userService.findLoginId(findIdReq.getName(), findIdReq.getPhoneNum());
        if (loginId != null) {
            return ResponseEntity.ok(new FindIdRep(loginId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("Reset Password")
    @PostMapping("/password-reset")
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

    @ApiOperation("LoginId DoubleCheck")
    @PostMapping("/main/join/doublecheck")
    public String idDoubleCheckPage(@RequestParam(value = "userID") String userId) {
        if (userService.checkId(userId)) {
            return "사용할 수 없는 아이디입니다.";
        } else {
            return "사용할 수 있는 아이디입니다.";
        }
    }

    @ApiOperation("MarketName Double Check")
    @PostMapping("/main/join/seller/doublecheck")
    public String MarketNameDoubleCheckPage(@RequestParam(value = "name") String name) {
        if (userService.checkName(name)) {
            return "사용할 수 없는 매장명 입니다.";
        } else {
            return "사용할 수 있는 매장명 입니다.";
        }
    }
}
