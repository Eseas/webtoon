package com.webtoon.controller.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.Login.SocialLogin.GoogleAccountProfileResponse;
import com.webtoon.dto.Login.SocialLogin.NaverLoginAPIProfileResponse;
import com.webtoon.dto.Login.SocialLogin.SocialLoginAuthResponse;
import com.webtoon.security.JwtUtils;
import com.webtoon.service.SocialLogin.SocialLoginService;
import com.webtoon.service.User.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class SocialLoginController {

    private final UserService userService;

    private final SocialLoginService socialLoginService;

    private final JwtUtils jwtUtils;

    @GetMapping("/signin/oauth2/code/google")
    public String getGoogleSignin(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String error_description,
            @RequestParam(name = "state", required = false) String state,
            Model model,
            HttpServletResponse response
    ) throws Exception {
        /**
         * 1. 구글 인증 코드를 받는다.
         * 2. 구글 인증 코드를 통해 google Access Token을 받아온다.
         * 3. Access Token을 사용하여 사용자의 정보를 받아온다.
         * 4. 사용자의 정보를 member 테이블에서 찾는다.
         * 5. 사용자의 정보가 member 테이블에 존재하지 않는다면, 신규 가입 페이지로 이동한다.
         * 6. 사용자의 정보가 member 테이블에 존재한다면, 서버 Access Token을 쿠키에 담은 후 홈으로 이동시킨다.
         */

        if(!(error == null || error.isEmpty())) {
            log.warn("error : processing login naver = {}",error);
            log.warn("error_description : processing login naver = {}",error_description);
            model.addAttribute("msg", error_description);
            return "closecurrentpage";
        }

        String accessToken = socialLoginService.getAccessToken(code, "google");
        GoogleAccountProfileResponse accountProfile = socialLoginService.getGoogleUserInfo(accessToken);
        log.info("accountProfile = {}", accountProfile);
        if(accessToken != null || accountProfile.isVerified_email()) {

            Optional<Member> member = userService.getGoogleMemberInDB(accountProfile.getId());

            String encryptAccessToken = jwtUtils.generateAccessToken(accountProfile.getId(), "USER", "SC002");
            String encryptRefreshToken = jwtUtils.generateRefreshToken(accountProfile.getId(), "USER", "SC002");
            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);

            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(600);

            response.addCookie(cookie);
            return "closecurrentpage";
        } else {
            String msg = "로그인에_실패했습니다.";
            model.addAttribute("msg", msg);
            return "redirect:/signin";
        }
    }

    @GetMapping("/signin/oauth2/code/kakao")
    public String getKakaoSignin(
            @RequestParam SocialLoginAuthResponse socialLoginAuthResponse,
            Model model,
            HttpServletResponse response
    ) throws Exception {
        if(!(socialLoginAuthResponse.getError() == null || socialLoginAuthResponse.getError().isEmpty())) {
            log.warn("error : processing login naver = {}",socialLoginAuthResponse.getError());
            log.warn("error_description : processing login naver = {}",socialLoginAuthResponse.getError_description());
            model.addAttribute("msg", socialLoginAuthResponse.getError_description());
            return "closecurrentpage";
        }

        String accessToken = socialLoginService.getAccessToken(socialLoginAuthResponse.getCode(), "google");
        GoogleAccountProfileResponse accountProfile = socialLoginService.getGoogleUserInfo(accessToken);

        if(accessToken != null || accountProfile.isVerified_email()) {
            Optional<Member> member = userService.getGoogleMemberInDB(accountProfile.getId());

            String encryptAccessToken = jwtUtils.generateAccessToken(accountProfile.getId(), "USER", "SC002");
            String encryptRefreshToken = jwtUtils.generateRefreshToken(accountProfile.getId(), "USER", "SC002");
            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);

            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(600);

            response.addCookie(cookie);
            return "closecurrentpage";
        } else {
            String msg = "로그인에_실패했습니다.";
            model.addAttribute("msg", msg);
            return "redirect:/signin";
        }
    }

    @GetMapping("/signin/oauth2/code/naver")
    public String getNaverSignin(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String error_description,
            Model model,
            HttpServletResponse response
    ) throws Exception {

        if(!(error == null || error.isEmpty())) {
            log.warn("error : processing login naver = {}",error);
            log.warn("error_description : processing login naver = {}",error_description);
            model.addAttribute("msg", error_description);
            return "closecurrentpage";
        }

        String accessToken = socialLoginService.getAccessToken(code, "naver");
        NaverLoginAPIProfileResponse userInfo = (NaverLoginAPIProfileResponse) socialLoginService.getSocialUserInfo(accessToken, "naver");

        log.info("Naver Social Login User Info : {}", userInfo);

        String encryptAccessToken = jwtUtils.generateAccessToken("test_user", "USER", "SC003");
        String encryptRefreshToken = jwtUtils.generateRefreshToken("test_user", "USER", "SC003");
        // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);

        Cookie cookie = new Cookie("accessToken", encryptAccessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(600);
        response.addCookie(cookie);
        return "closecurrentpage";
    }
}
