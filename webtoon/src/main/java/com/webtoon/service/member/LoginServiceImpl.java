package com.webtoon.service.member;

import com.webtoon.domain.entity.Member;
import com.webtoon.domain.login.LoginDto;
import com.webtoon.infrastructure.member.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberReader memberReader;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Member login(LoginDto.Request request) {
        Member member = memberReader.readByloginId(request.getLoginId());

        // TODO - 회원가입 기능 이후 주석 해제
//        bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword());

        return member;
    }
}
