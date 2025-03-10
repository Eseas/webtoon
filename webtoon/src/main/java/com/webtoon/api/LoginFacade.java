package com.webtoon.api;

import com.webtoon.domain.entity.Member;
import com.webtoon.domain.login.Login;
import com.webtoon.infrastructure.member.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFacade {
    private final LoginService loginService;

    public Member login(Login.Request request, HttpSession session) {
        Member member = loginService.login(request);
        session.setAttribute("user", member);

        return member;
    }
}
