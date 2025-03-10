package com.webtoon.controller;

import com.webtoon.domain.login.LoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/signin")
    public ResponseEntity<String> singin(LoginDto.Request loginDto) {
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
