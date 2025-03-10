package com.webtoon.sociallogin;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.Login.SocialLogin.GoogleAccountProfileResponse;
import com.webtoon.dto.Login.SocialLogin.LoginAPIProfileResponse;

import java.util.Optional;

public interface SocialLoginService {
    String getAccessToken(String author_code, String social_code) throws Exception;
    LoginAPIProfileResponse getSocialUserInfo(String accessToken, String socialCode) throws Exception;
    Optional<Member> getSocialMemberInDB(String id, String socialCode) throws Exception;
    boolean memberInsertInDB(LoginAPIProfileResponse userInfo, String SocialCode) throws Exception;
}