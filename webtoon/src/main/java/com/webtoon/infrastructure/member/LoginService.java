package com.webtoon.infrastructure.member;

import com.webtoon.domain.entity.Member;
import com.webtoon.domain.login.LoginDto;

public interface LoginService {
    Member login(LoginDto.Request request);
}
