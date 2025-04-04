package com.webtoon.api;

import com.webtoon.domain.entity.Member;
import com.webtoon.domain.login.Login;
import com.webtoon.infrastructure.member.LoginService;
import com.webtoon.infrastructure.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFacade {

    private final MemberService memberService;

    private final LoginService loginService;

    public void existId(String loginId) {
        memberService.existId(loginId);
    }

    public Member login(Login.Request request, HttpSession session) {
        Member member = loginService.login(request);
        session.setAttribute("user", member);

        return member;
    }
}
