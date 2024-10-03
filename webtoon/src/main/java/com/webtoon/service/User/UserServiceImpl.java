package com.webtoon.service.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.domain.User.Member;
import com.webtoon.dto.*;
import com.webtoon.repository.jpa.MemberRepository;
import com.webtoon.security.JwtUtils;
import com.webtoon.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
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
    public String getAccessToken(
            String author_code,
            String socialCode
    ) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        String url;

        switch(socialCode) {
            case "naver":
                body.add("grant_type", "authorization_code");
                body.add("client_id", naverClientId);
                body.add("client_secret", naverClientPwd);
                body.add("code", author_code);
                body.add("state", "test");
                url = "https://nid.naver.com/oauth2.0/token";
                break;
            case "google":
                body.add("client_id", googleClientId);
                body.add("client_secret", googleSecretPassword);
                body.add("code", author_code);
                body.add("grant_type", "authorization_code");
                body.add("redirect_uri", googleRedirectUri);
                url = "https://oauth2.googleapis.com/token";
                break;
            case "kakao":
                url = "https://kauth.kakao.com/oauth/token";
                break;
            default:
                return "";
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url, request, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 구글로부터 받은 JSON을 파싱
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // access_token과 refresh_token을 추출
            String accessToken = jsonNode.get("access_token").asText();
//            String refreshToken = jsonNode.get("refresh_token").asText();
            // redisUtils.setDataTo0(accessToken, refreshToken);
            return accessToken;
        } catch (JsonProcessingException e) {
            log.warn("Login Process Failed : {}", e.getMessage());
            return null;
        }
    }

    @Override
    public GoogleAccountProfileResponse getGoogleUserInfo(String accessToken
    ) throws Exception {
        if(accessToken == null || accessToken.isEmpty()) {
            log.warn("accessToken is null or empty");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String body = response.getBody();
            log.info("body : {}", body);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(body);

                String id = jsonNode.has("sub") ? jsonNode.get("sub").asText() : null;
                String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
                boolean verifiedEmail = jsonNode.has("email_verified") && jsonNode.get("email_verified").asBoolean();
                String givenName = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
                String familyName = jsonNode.has("given_name") ? jsonNode.get("given_name").asText() : null;

                return GoogleAccountProfileResponse.builder()
                        .id(id)
                        .email(email)
                        .verified_email(verifiedEmail)
                        .given_name(givenName)
                        .family_name(familyName)
                        .build();
            } catch (JsonProcessingException e) {
                log.warn("Google User Data Get Process Failed : {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public LoginAPIProfileResponse getSocialUserInfo(
            String accessToken,
            String socialCode
    ) throws Exception {
        if(accessToken == null || accessToken.isEmpty()) {
            log.warn("accessToken is null or empty");
            return null;
        }

        String url;

        switch(socialCode) {
            case "google":
                url = "https://www.googleapis.com/oauth2/v3/userinfo";
                break;
            case "naver":
                url = "https://openapi.naver.com/v1/nid/me";
                break;
            case "kakao":
                url = "https://kapi.kakao.com/v2/user/me";
                break;
            default:
                return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if(!(response.getStatusCode() == HttpStatus.OK)) {
            log.warn("Processing Social Login Failed");
            return null;
        }

        String body = response.getBody();
        log.info("body : {}", body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        LoginAPIProfileResponse userInfo;
        switch(socialCode) {
            case "google":
                return userInfo = new GoogleLoginAPIProfileResponse(jsonNode);
            case "naver":
                return userInfo = new NaverLoginAPIProfileResponse(jsonNode);
            case "kakao":
                return new KakaoLoginAPIProfileResponse(jsonNode);
            default:
                return null;
        }
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
