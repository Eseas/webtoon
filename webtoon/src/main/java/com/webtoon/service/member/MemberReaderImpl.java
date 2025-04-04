package com.webtoon.service.member;

import com.webtoon.domain.entity.Member;
import com.webtoon.exception.BusinessException;
import com.webtoon.infrastructure.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberReaderImpl implements MemberReader {

    private final MemberRepository memberRepository;

    @Override
    public boolean existMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    @Override
    public Member readByloginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(BusinessException::new);
    }
}
