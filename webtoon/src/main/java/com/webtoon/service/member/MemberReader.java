package com.webtoon.service.member;

import com.webtoon.domain.entity.Member;

public interface MemberReader {

    Member readByloginId(String loginId);
}
