package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.GoogleAccountProfileResponse;
import com.webtoon.dto.LoginAPIProfileResponse;
import com.webtoon.dto.LoginDto;

import java.util.Optional;

public interface UserService {
    boolean loginCheck(LoginDto loginDto) throws Exception;
    String getAccessToken(String author_code, String social_code) throws Exception;
    Optional<Member> getMemberInDB(String email) throws Exception;
    Optional<Member> getGoogleMemberInDB(String loginId) throws Exception;
    GoogleAccountProfileResponse getGoogleUserInfo(String accessToken) throws Exception;
    public LoginAPIProfileResponse getSocialUserInfo(String accessToken, String socialCode) throws Exception;
    Member saveMemberInDB(Member member, String socialCode) throws Exception;
}
