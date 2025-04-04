package com.webtoon.service.member;

import com.webtoon.exception.BusinessException;
import com.webtoon.infrastructure.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberReader memberReader;

    @Override
    public void existId(String loginId) {
        // 존재하면 true
        if(memberReader.existMemberByLoginId(loginId)) {
            throw new BusinessException("이미 존재하는 id입니다.");
        }
    }
}
