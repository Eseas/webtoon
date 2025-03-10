package com.webtoon.infrastructure.member;

import com.webtoon.domain.entity.Member;
import com.webtoon.domain.login.Login;

public interface LoginService {
    Member login(Login.Request request);
}
