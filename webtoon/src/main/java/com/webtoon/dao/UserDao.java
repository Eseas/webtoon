package com.webtoon.dao;

import com.webtoon.domain.User.LoginDto;
import com.webtoon.domain.User.UserDto;

public interface UserDao {
    boolean login(LoginDto loginDto) throws Exception;
    int signUp(UserDto userDto) throws Exception;
}
