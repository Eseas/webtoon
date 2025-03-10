package com.webtoon.controller;

import com.webtoon.api.LoginFacade;
import com.webtoon.domain.login.Login;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginFacade loginFacade;

    @PostMapping("/signin")
    public ResponseEntity<String> singin(
            @RequestBody Login.Request loginDto,
            HttpSession session
    ) {
        loginFacade.login(loginDto, session);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
