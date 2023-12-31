package com.shop.onlyfit.service;

import com.shop.onlyfit.auth.KakaoProfile;
import com.shop.onlyfit.auth.OAuthToken;
import com.shop.onlyfit.auth.jwt.UnConnected;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${kakao.admin}")
    String admin;


    public String getAccessToken(String code, String redirectUri, String clientId) {
        OAuthToken oauthToken = getOauthToken(code, redirectUri, clientId);
        return oauthToken.getAccessToken();
    }

    private OAuthToken getOauthToken(String code, String redirectUri, String apiKey) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        String grantType = "authorization_code";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("grant_type", grantType);
        paramMap.add("client_id", apiKey);
        paramMap.add("redirect_uri", redirectUri);
        paramMap.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramMap, headers);
        ResponseEntity<OAuthToken> responseEntity = restTemplate.postForEntity(tokenUrl, request, OAuthToken.class);

        return responseEntity.getBody();
    }

    public User getAuthenticatedUser(String accessToken) {
        KakaoProfile kakaoProfile = getKakaoProfile(accessToken);
        return saveKakaoUserInfo(kakaoProfile);
    }

    public KakaoProfile getKakaoProfile(String accessToken) {
        String profileUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<KakaoProfile> responseEntity = restTemplate.exchange(profileUrl, HttpMethod.GET, requestEntity, KakaoProfile.class);

        return responseEntity.getBody();
    }

    public User saveKakaoUserInfo(KakaoProfile kakaoProfile) {

        String randomStr = UUID.randomUUID().toString();
        String kakaoPassword = encoder.encode(randomStr);

        KakaoProfile.KakaoAccount kakaoAccount = kakaoProfile.getKakaoAccount();
        Long kakaoId = kakaoProfile.getId();

        Optional<User> optionalUser = userRepository.findByLoginId(String.valueOf(kakaoId)); //이미 가입된 사용자인지 확인

        //이미 가입된 사용자라면 리턴
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }


        User newUser = new User();
        newUser.setLoginId(String.valueOf(kakaoId));
        newUser.setPassword(kakaoPassword);
        newUser.setEmail(kakaoAccount.getEmail());
        newUser.setSex(kakaoAccount.getGender());
        newUser.setLoginType(LoginType.KAKAO);
        newUser.setName("kakao_" + kakaoProfile.getProperties().getNickname());

        return userRepository.save(newUser);
    }

    public UnConnected kakaoUnconnected(Long tId) {

        String targetUrl = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + admin);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("target_id_type", "user_id");
        map.add("target_id", tId);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<UnConnected> response = restTemplate.postForEntity(targetUrl, request, UnConnected.class);
        return response.getBody();
    }
}