package com.webtoon.repository.dao;

import com.webtoon.dto.LoginDto;
import com.webtoon.domain.User.Member;

public interface UserDao {
    boolean login(LoginDto loginDto) throws Exception;
    int insertMemberToDB(Member Member) throws Exception;
}
