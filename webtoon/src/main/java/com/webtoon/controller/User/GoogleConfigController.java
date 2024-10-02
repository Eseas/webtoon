package com.webtoon.controller.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleConfigController {
    @Value("${google.client_id}")
    private String googleClientId;
    @Value("${google.secret_password}")
    private String googleClientPwd;
    @Value("${google.redirect_uri}")
    private String googleRedirectUrl;

    @GetMapping("/api/google-config")
    public Map<String, String> getGoogleConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", googleClientId);
        config.put("redirectUrl", googleRedirectUrl);
        return config;
    }
}
