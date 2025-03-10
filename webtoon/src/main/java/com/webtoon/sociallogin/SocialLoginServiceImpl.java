//package com.webtoon.sociallogin;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.webtoon.domain.User.Member;
//import com.webtoon.dto.Login.SocialLogin.*;
//import com.webtoon.repository.jpa.MemberRepository;
//import com.webtoon.utils.RedisUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//@Slf4j
//public class SocialLoginServiceImpl implements SocialLoginService {
//    private final RedisUtils redisUtils;
//    private final MemberRepository memberRepository;
//
//    @Value("${google.client_id}")
//    private String googleClientId;
//    @Value("${google.secret_password}")
//    private String googleSecretPassword;
//    @Value("${google.redirect_uri}")
//    private String googleRedirectUri;
//
//    @Value("${naver.client_id}")
//    private String naverClientId;
//    @Value("${naver.secret_password}")
//    private String naverClientPwd;
//    @Value("${naver.redirect_uri}")
//    private String naverRedirectUrl;
//
//    @Value("${kakao.rest_api_key}")
//    private String kakaoClientId;
//    @Value("${kakao.redirect_uri}")
//    private String kakaoRedirectUri;
//
//    @Override
//    public String getAccessToken(
//            String author_code,
//            String socialCode
//    ) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        String url;
//
//        switch(socialCode) {
//            case "SC002":
//                body.add("client_id", googleClientId);
//                body.add("client_secret", googleSecretPassword);
//                body.add("code", author_code);
//                body.add("grant_type", "authorization_code");
//                body.add("redirect_uri", googleRedirectUri);
//                url = "https://oauth2.googleapis.com/token";
//                break;
//            case "SC003":
//                body.add("grant_type", "authorization_code");
//                body.add("client_id", naverClientId);
//                body.add("client_secret", naverClientPwd);
//                body.add("code", author_code);
//                body.add("state", "test");
//                url = "https://nid.naver.com/oauth2.0/token";
//                break;
//            case "SC004":
//                body.add("grant_type", "authorization_code");
//                body.add("client_id", kakaoClientId);
//                body.add("code", author_code);
//                body.add("state", "test");
//                url = "https://kauth.kakao.com/oauth/token";
//                break;
//            default:
//                return "";
//        }
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(
//                url, request, String.class);
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//
//            // access_token과 refresh_token을 추출
//            String accessToken = jsonNode.get("access_token").asText();
//            //String refreshToken = jsonNode.get("refresh_token").asText();
//            //redisUtils.setDataTo0(accessToken, refreshToken);
//            return accessToken;
//        } catch (JsonProcessingException e) {
//            log.warn("Login Process Failed : {}", e.getMessage());
//            return "";
//        }
//    }
//
//    @Override
//    public LoginAPIProfileResponse getSocialUserInfo(
//            String accessToken,
//            String socialCode
//    ) throws Exception {
//        if(accessToken == null || accessToken.isEmpty()) {
//            log.warn("accessToken is null or empty");
//            return null;
//        }
//
//        String url;
//
//        switch(socialCode) {
//            case "SC002":   // google
//                url = "https://www.googleapis.com/oauth2/v3/userinfo";
//                break;
//            case "SC003":   // naver
//                url = "https://openapi.naver.com/v1/nid/me";
//                break;
//            case "SC004":   // kakao
//                url = "https://kapi.kakao.com/v2/user/me";
//                break;
//            default:
//                return null;
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        if(!(response.getStatusCode() == HttpStatus.OK)) {
//            log.warn("Processing Social Login Failed");
//            return null;
//        }
//
//        String body = response.getBody();
//        log.info("body : {}", body);
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(body);
//        return switch (socialCode) {
//            case "SC002" ->   // google
//                    new GoogleLoginAPIProfileResponse(jsonNode);
//            case "SC003" ->   // naver
//                    new NaverLoginAPIProfileResponse(jsonNode);
//            case "SC004" ->   // kakao
//                    new KakaoLoginAPIProfileResponse(jsonNode);
//            default -> null;
//        };
//    }
//
//    @Override
//    public Optional<Member> getSocialMemberInDB(String id, String socialCode) throws Exception {
//        return Optional.empty();
//    }
//
//    @Override
//    @Transactional
//    public boolean memberInsertInDB(LoginAPIProfileResponse userInfo,
//                                    String SocialCode
//    ) throws Exception {
//        if(userInfo == null || SocialCode == null) {
//            log.info("error : processing insert db");
//            return false;
//        }
//
//        if(IsSocialMemberInDB(userInfo.getLoginId(), SocialCode)) {
//            log.info("existed user");
//            return false;
//        }
//
//        switch(SocialCode) {
//            case "SC002":   // google
//                GoogleLoginAPIProfileResponse googleUserInfo = (GoogleLoginAPIProfileResponse)userInfo;
//                Member googleMember = Member.builder()
//                        .loginId(googleUserInfo.getLoginId())
//                        .name(googleUserInfo.getName())
//                        .role("USER")
//                        .social_code("SC002")
//                        .using_state("US001")
//                        .created_id("admin")
//                        .updated_id("admin")
//                        .build();
//                memberRepository.save(googleMember);
//                break;
//            case "SC003":   // naver
//                NaverLoginAPIProfileResponse naverUserInfo = (NaverLoginAPIProfileResponse) userInfo;
//                Member naverMember = Member.builder()
//                        .loginId(naverUserInfo.getLoginId())
//                        .name(naverUserInfo.getName())
//                        .gender(naverUserInfo.getGender())
//                        .birth(naverUserInfo.getBirthYear())
//                        .age(calculateAge(naverUserInfo.getBirthYear()))
//                        .ageRange(naverUserInfo.getAge())
//                        .using_state("US001")
//                        .role("USER")
//                        .social_code("SC003")
//                        .created_id("admin")
//                        .updated_id("admin")
//                        .build();
//                memberRepository.save(naverMember);
//                break;
//            case "SC004":   // kakao
//                KakaoLoginAPIProfileResponse kakaoUserInfo = (KakaoLoginAPIProfileResponse) userInfo;
//                Member kakaoMember = Member.builder()
//                        .loginId(kakaoUserInfo.getLoginId())
//                        .name(kakaoUserInfo.getName())
//                        .birth(kakaoUserInfo.getBirth())
//                        .age(calculateAge(kakaoUserInfo.getBirth()))
//                        .using_state("US001")
//                        .role("USER")
//                        .social_code("SC004")
//                        .created_id("admin")
//                        .updated_id("admin")
//                        .build();
//
//                memberRepository.save(kakaoMember);
//                break;
//        }
//        return false;
//    }
//
//    private static int calculateAge(String birthYearString) {
//        // String을 int로 변환
//        int birthYear = Integer.parseInt(birthYearString);
//
//        // 현재 년도 구하기
//        int currentYear = LocalDate.now().getYear();
//
//        // 나이 계산
//        int age = currentYear - birthYear;
//
//        return age;
//    }
//
//    private boolean IsSocialMemberInDB(
//            String loginId,
//            String socialCode
//    ) throws Exception {
//        Optional<Member> member = memberRepository.findBySocialLoginIdAndUsingState(loginId, "US001", socialCode);
//
//        if(member.isPresent()) {
//            return true;
//        }
//        log.info("not found user");
//        return false;
//    }
//
//}
