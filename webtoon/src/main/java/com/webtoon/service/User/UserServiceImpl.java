package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.LoginDto;
import com.webtoon.repository.jpa.MemberRepository;
import com.webtoon.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public boolean loginCheck(LoginDto loginDto) throws Exception {
        /**
         * 1. LoginDto.Id로 member를 찾는다.
         * 2. using_state를 체크한다.
         * 3. 비밀번호를 확인한다.
         * 4. 결과를 return한다.
         */
        Optional<Member> member = memberRepository.findByLoginIdAndUsingState(loginDto.getId(), "US001");
        if (     member.isPresent() &&
                loginDto.getPwd().equals(member.get().getPwd())
                 //passwordEncoder.matches(loginDto.getPwd(), member.get().getPwd())
        ) {
            log.info("loginDto.getId() : {}", loginDto.getId());
            return true;
        }
        return false;
    }

    public Optional<Member> getMemberById(String id) {
        return memberRepository.findByLoginId(id);
    }

    public void loginHandler(String id) {
        Optional<Member> member = memberRepository.findByLoginId(id);

        if(member.isPresent()) {
            try {
                member.get().resetFailureCount();
                memberRepository.save(member.get());
            } catch (Exception e) {
            }
        }
    }

    private void loginSuccessHandler(Member member) {
        try {
            member.resetFailureCount();
            memberRepository.save(member);
        } catch (Exception e) {

        }
    }
}
