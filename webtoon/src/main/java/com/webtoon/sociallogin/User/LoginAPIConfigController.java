package com.webtoon.sociallogin.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginAPIConfigController {
    @Value("${google.client_id}")
    private String googleClientId;
    @Value("${google.secret_password}")
    private String googleClientPwd;
    @Value("${google.redirect_uri}")
    private String googleRedirectUrl;

    @Value("${naver.client_id}")
    private String naverClientId;
    @Value("${naver.secret_password}")
    private String naverClientPwd;
    @Value("${naver.redirect_uri}")
    private String naverRedirectUrl;

    @Value("${kakao.rest_api_key}")
    private String kakaoRestApiKey;
    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUrl;

    @GetMapping("/api/google-config")
    public Map<String, String> getGoogleConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", googleClientId);
        config.put("redirectUrl", googleRedirectUrl);
        return config;
    }

    @GetMapping("api/naver-config")
    public Map<String, String> getNaverConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", naverClientId);
        config.put("redirectUrl", naverRedirectUrl);
        return config;
    }

    @GetMapping("api/kakao-config")
    public Map<String, String> getKakaoConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", kakaoRestApiKey);
        config.put("redirectUrl", kakaoRedirectUrl);
        return config;
    }
}
