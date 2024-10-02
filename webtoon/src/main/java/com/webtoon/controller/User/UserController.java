package com.webtoon.controller.User;

import com.webtoon.dto.LoginDto;
import com.webtoon.domain.User.Member;
import com.webtoon.dto.LoginFormDto;
import com.webtoon.security.CustomUserDetails;
import com.webtoon.service.User.UserService;
import com.webtoon.security.JwtUtils;
import com.webtoon.utils.RedisUtils;
import com.webtoon.validator.LoginValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private LoginValidator loginValidator;
    @Autowired
    private AuthenticationManager authenticationManager;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(loginValidator);
    }

    @GetMapping("/signin")
    public String getLogin(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());
        return "signin";
    }

    @PostMapping("/signin/process")
    public String postLogin(@Validated @ModelAttribute LoginFormDto loginFormDto,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request,
                                        HttpServletResponse response
    ) throws Exception {
        /**
         * 1. userService.loginCheck() 실행
         * 1-1. 로그인 실패 시
         *      1. pwd 실패 : pwd실패 횟수 +1 update 실행
         *                   만약 update 이후 틀린 횟수가 5회 이상이라면, using_state US002로 변경
         *      2. id 찾지 못함 : 에러 메시지 발생
         *      3. 상태 잠김 : 상태에 따른 에러 메시지 발생
         *                   비밀번호 횟수 5회 이상 잠금 에러 > 비밀번호 찾기 창으로 이동 후 비밀번호를 재설정해주세요.
         *                   휴면 계정 > 휴면 상태 해제 페이지로 이동할 수 있는 버튼 제공
         * 1-2. 로그인 성공 시
         *      1. AccessToken 발급, RefreshToken 발급
         *         Redis : 0에 Key : AccessToken, Value : RefreshToken 저장.
         *      2. 찾은 member 객체가 pwd 실패 횟수가 0이 아니라면, pwd 실패 횟수 0으로 초기화
         */
        if(bindingResult.hasErrors()) {
            log.warn("errors={}",bindingResult.getAllErrors());
            return "signin";
        }

        try {
            LoginDto loginDto = LoginDto.builder()
                    .id(loginFormDto.getId())
                    .pwd(loginFormDto.getPwd())
                    .build();

            if(!userService.loginCheck(loginDto)) {
                String msg = "Id 또는 Pwd를 다시 확인해주세요.";
                model.addAttribute("msg", msg);
                return "signin";
            }

            String encryptAccessToken = jwtUtils.generateAccessToken(loginDto.getId(), loginDto.getPwd());
            String encryptRefreshToken = jwtUtils.generateRefreshToken(loginDto.getId(), loginDto.getPwd());
            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);

            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(600);
            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("msg", "로그인 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "signin";
        }
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
