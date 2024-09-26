package com.webtoon.service.User;

import com.webtoon.dto.LoginDto;
import com.webtoon.utils.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtils jwtUtils;


    public boolean loginCheck(LoginDto loginDto) {

        return true;
    }
}
