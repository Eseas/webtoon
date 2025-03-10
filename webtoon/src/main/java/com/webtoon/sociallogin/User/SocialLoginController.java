//package com.webtoon.sociallogin.User;
//
//import com.webtoon.dto.Login.SocialLogin.*;
//import com.webtoon.security.JwtUtils;
//import com.webtoon.sociallogin.SocialLoginService;
//import com.webtoon.service.User.UserService;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//@Controller
//@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//@Slf4j
//public class SocialLoginController {
//
//    private final UserService userService;
//
//    private final SocialLoginService socialLoginService;
//
//    private final JwtUtils jwtUtils;
//
//    @GetMapping("/signin/oauth2/code/google")
//    public String getGoogleSignin(
//            @ModelAttribute SocialLoginAuthResponse socialLoginAuthResponse,
//            Model model,
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws Exception {
//        /**
//         * 1. 구글 인증 코드를 받는다.
//         * 2. 구글 인증 코드를 통해 google Access Token을 받아온다.
//         * 3. Access Token을 사용하여 사용자의 정보를 받아온다.
//         * 4. 사용자의 정보를 member 테이블에서 찾는다.
//         * 5. 사용자의 정보가 member 테이블에 존재하지 않는다면, 신규 가입 페이지로 이동한다.
//         * 6. 사용자의 정보가 member 테이블에 존재한다면, 서버 Access Token을 쿠키에 담은 후 홈으로 이동시킨다.
//         */
//
//        if(!(socialLoginAuthResponse.getError() == null || socialLoginAuthResponse.getError().isEmpty())) {
//            log.warn("error : processing login naver = {}", socialLoginAuthResponse.getError());
//            log.warn("error_description : processing login naver = {}",socialLoginAuthResponse.getError_description());
//            model.addAttribute("msg", socialLoginAuthResponse.getError_description());
//            return "closecurrentpage";
//        }
//
//        String accessToken = socialLoginService.getAccessToken(socialLoginAuthResponse.getCode(), "SC002");
//        GoogleLoginAPIProfileResponse accountProfile = (GoogleLoginAPIProfileResponse) socialLoginService.getSocialUserInfo(accessToken, "SC002");
//        log.info("accountProfile = {}", accountProfile);
//        if(accessToken != null) {
//            if(socialLoginService.memberInsertInDB(accountProfile, "SC002")) {
//                log.info("memberInsertDB success");
//            }
//
//            String encryptAccessToken = jwtUtils.generateAccessToken(accountProfile.getLoginId(), "USER", "SC002");
//            String encryptRefreshToken = jwtUtils.generateRefreshToken(accountProfile.getLoginId(), "USER", "SC002");
//            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);
//
//            HttpSession session = request.getSession();
//
//            session.setAttribute("accessToken", encryptAccessToken);
//
//            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(600);
//
//            response.addCookie(cookie);
//            return "closecurrentpage";
//        } else {
//            String msg = "로그인에_실패했습니다.";
//            model.addAttribute("msg", msg);
//            return "closecurrentpage";
//        }
//    }
//
//    @GetMapping("/signin/oauth2/code/kakao")
//    public String getKakaoSignin(
//            @ModelAttribute SocialLoginAuthResponse socialLoginAuthResponse,
//            Model model,
//            HttpSession session,
//            HttpServletResponse response
//    ) throws Exception {
//        if(!(socialLoginAuthResponse.getError() == null || socialLoginAuthResponse.getError().isEmpty())) {
//            log.warn("error : processing login kakao = {}",socialLoginAuthResponse.getError());
//            log.warn("error_description : processing login kakao = {}",socialLoginAuthResponse.getError_description());
//            model.addAttribute("msg", socialLoginAuthResponse.getError_description());
//            return "closecurrentpage";
//        }
//
//        String accessToken = socialLoginService.getAccessToken(socialLoginAuthResponse.getCode(), "SC004");
//        KakaoLoginAPIProfileResponse accountProfile = (KakaoLoginAPIProfileResponse) socialLoginService.getSocialUserInfo(accessToken, "SC004");
//
//        if(accessToken != null) {
//            if(socialLoginService.memberInsertInDB(accountProfile, "SC004")) {
//                log.info("memberInsertDB success");
//            }
//            String encryptAccessToken = jwtUtils.generateAccessToken(accountProfile.getLoginId(), "USER", "SC004");
//            String encryptRefreshToken = jwtUtils.generateRefreshToken(accountProfile.getLoginId(), "USER", "SC004");
//            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);
//
//            session.setAttribute("accessToken", encryptAccessToken);
//
//            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(600);
//
//            response.addCookie(cookie);
//            return "closecurrentpage";
//        } else {
//            String msg = "로그인에_실패했습니다.";
//            model.addAttribute("msg", msg);
//            return "closecurrentpage";
//        }
//    }
//
//    @GetMapping("/signin/oauth2/code/naver")
//    public String getNaverSignin(
//            @ModelAttribute SocialLoginAuthResponse socialLoginAuthResponse,
//            Model model,
//            HttpSession session,
//            HttpServletResponse response
//    ) throws Exception {
//        if(!(socialLoginAuthResponse.getError() == null || socialLoginAuthResponse.getError().isEmpty())) {
//            log.warn("error : processing login kakao = {}",socialLoginAuthResponse.getError());
//            log.warn("error_description : processing login kakao = {}",socialLoginAuthResponse.getError_description());
//            model.addAttribute("msg", socialLoginAuthResponse.getError_description());
//            return "closecurrentpage";
//        }
//
//        String accessToken = socialLoginService.getAccessToken(socialLoginAuthResponse.getCode(), "SC003");
//        NaverLoginAPIProfileResponse accountProfile = (NaverLoginAPIProfileResponse) socialLoginService.getSocialUserInfo(accessToken, "SC003");
//
//        if(accessToken != null) {
//            if(socialLoginService.memberInsertInDB(accountProfile, "SC004")) {
//                log.info("memberInsertDB success");
//            }
//            String encryptAccessToken = jwtUtils.generateAccessToken(accountProfile.getLoginId(), "USER", "SC003");
//            String encryptRefreshToken = jwtUtils.generateRefreshToken(accountProfile.getLoginId(), "USER", "SC003");
//            // redisUtils.setDataTo0(encryptAccessToken, encryptRefreshToken);
//
//            session.setAttribute("accessToken", encryptAccessToken);
//
//            Cookie cookie = new Cookie("accessToken", encryptAccessToken);
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(600);
//            response.addCookie(cookie);
//            return "closecurrentpage";
//        } else {
//            String msg = "로그인에_실패했습니다.";
//            model.addAttribute("msg", msg);
//            return "closecurrentpage";
//        }
//    }
//}
