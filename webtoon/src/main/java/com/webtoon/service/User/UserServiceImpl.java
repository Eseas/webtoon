package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.LoginDto;
import com.webtoon.repository.jpa.MemberRepository;
import com.webtoon.utils.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public boolean loginCheck(LoginDto loginDto) {
        /**
         * 1. LoginDto.Id로 member를 찾는다.
         * 2. using_state를 체크한다.
         * 3. 비밀번호를 확인한다.
         * 4. 결과를 return한다.
         */
        Optional<Member> member = memberRepository.findByLoginId(loginDto.getId());

        if (     member.isPresent() &&
                 Objects.equals(member.get().getUsing_state(), "US001") &&
                 passwordEncoder.matches(loginDto.getPwd(), member.get().getPwd())
        ) {
            return true;
        }
        return false;
    }
}
