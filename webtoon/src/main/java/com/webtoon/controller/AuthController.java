package com.webtoon.controller;

import com.webtoon.api.AuthFacade;
import com.webtoon.domain.login.Login;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    @GetMapping("/{id}/exists")
    public ResponseEntity<String> existId(
            @PathVariable("id") String loginId
    ) {
        authFacade.existId(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(loginId);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(
            @RequestBody Login.Request loginDto,
            HttpSession session
    ) {
        authFacade.login(loginDto, session);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
