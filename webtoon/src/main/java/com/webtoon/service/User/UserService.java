package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.GoogleAccountProfileResponse;
import com.webtoon.dto.LoginDto;

public interface UserService {
    boolean loginCheck(LoginDto loginDto) throws Exception;
    String getGoogleAccessToken(String code) throws Exception;
    boolean checkMemberInDB(String email) throws Exception;
    GoogleAccountProfileResponse getGoogleUserInfo(String accessToken) throws Exception;
}
