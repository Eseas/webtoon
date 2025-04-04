package com.webtoon.service.member;

import com.webtoon.domain.entity.Member;

public interface MemberReader {

    boolean existMemberByLoginId(String loginId);

    Member readByloginId(String loginId);
}
