package com.webtoon.service.User;

import com.webtoon.domain.User.Member;
import com.webtoon.dto.Login.LoginDto;
import com.webtoon.repository.jpa.MemberRepository;
import com.webtoon.security.JwtUtils;
import com.webtoon.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtUtils jwtUtils;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisUtils redisUtils;

    @Value("${google.client_id}")
    private String googleClientId;
    @Value("${google.secret_password}")
    private String googleSecretPassword;
    @Value("${google.redirect_uri}")
    private String googleRedirectUri;

    @Value("${naver.client_id}")
    private String naverClientId;
    @Value("${naver.secret_password}")
    private String naverClientPwd;
    @Value("${naver.redirect_uri}")
    private String naverRedirectUrl;

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

    @Override
    public Optional<Member> getMemberInDB(String loginId
    ) throws Exception {
        return memberRepository.findByLoginIdAndUsingState(loginId, "US001");
    }

    @Override
    public Optional<Member> getGoogleMemberInDB(String loginId) throws Exception {
        return memberRepository.findByGoogleLoginIdAndUsingState(loginId, "US001", "AC002")
                .or(() -> Optional.empty());
    }

    @Override
    @Transactional
    public Member saveMemberInDB(Member member, String socialCode) throws Exception {
        Member existMember = memberRepository
                .findByLoginIdAndUsingState(member.getLoginId(), "US001")
                .orElseGet(() -> Member.builder().build());
        return member;
    }

    private void loginSuccessHandler(Member member
    ) {
        try {
            member.resetFailureCount();
            memberRepository.save(member);
        } catch (Exception e) {

        }
    }
}
