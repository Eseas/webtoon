package com.webtoon.controller.User;

import com.webtoon.dto.LoginDto;
import com.webtoon.domain.User.Member;
import com.webtoon.service.User.UserService;
import com.webtoon.utils.JWT.JwtUtils;
import com.webtoon.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/signin")
    public String login() {
        return "signin";
    }

    @PostMapping("/signin")
    public String login(LoginDto loginDto,
                                        HttpServletRequest request,
                                        HttpServletResponse response
    ) throws Exception {
        /**
         * 1. userService.loginCheck() 실행
         * 1-1. 로그인 성공 시
         *      AccessToken 발급, RefreshToken 발급
         *      Redis : 0에 Key : AccessToken, Value : RefreshToken 저장.
         *
         */
        if(userService.loginCheck(loginDto)) {
            String encryptAccessToken = jwtUtils.generateAccessToken(loginDto.getId(), loginDto.getPwd());
            String encryptRefreshToken = jwtUtils.generateRefreshToken(loginDto.getId(), loginDto.getPwd());
            redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);
            return "/";
        }
        return "redirect:/signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PutMapping("/signup")
    public String signup(Member member,
                                         HttpServletResponse response
    ) throws Exception {
        return "/";
    }
}
