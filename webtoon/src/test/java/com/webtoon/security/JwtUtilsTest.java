package com.webtoon.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Test
    void generateAccessToken() throws Exception {
        String accessToken = jwtUtils.generateAccessToken("asdf", "USER", "AC001");

        log.info("access token: {}", accessToken);
    }

    @Test
    void generateRefreshToken() {
    }

    @Test
    void validateJWT() {
    }
}