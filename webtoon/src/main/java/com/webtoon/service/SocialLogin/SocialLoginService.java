package com.webtoon.service.SocialLogin;

import com.webtoon.dto.Login.SocialLogin.GoogleAccountProfileResponse;
import com.webtoon.dto.Login.SocialLogin.LoginAPIProfileResponse;

public interface SocialLoginService {
    String getAccessToken(String author_code, String social_code) throws Exception;
    GoogleAccountProfileResponse getGoogleUserInfo(String accessToken) throws Exception;
    public LoginAPIProfileResponse getSocialUserInfo(String accessToken, String socialCode) throws Exception;

}
