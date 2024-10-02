package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.LoginDto;

public interface UserService {
    boolean loginCheck(LoginDto loginDto) throws Exception;
}
