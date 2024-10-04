package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.Login.LoginDto;

import java.util.Optional;

public interface UserService {
    boolean loginCheck(LoginDto loginDto) throws Exception;
    Optional<Member> getMemberInDB(String email) throws Exception;
    Optional<Member> getGoogleMemberInDB(String loginId) throws Exception;
    Member saveMemberInDB(Member member, String socialCode) throws Exception;
}
