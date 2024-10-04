package com.webtoon.repository.dao;

import com.webtoon.dto.Login.LoginDto;
import com.webtoon.domain.User.Member;

public interface UserDao {
    boolean login(LoginDto loginDto) throws Exception;
    int insertMemberToDB(Member Member) throws Exception;
}
