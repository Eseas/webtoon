package com.webtoon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class JwtUtils {
    @Value("${custom.jwt.secret_key}")
    private String secretKey;

    @Value("${custom.AES_256_GCM_KEY}")
    private String AES_256_GCM_KEY;

    private long accessTokenValiditySeconds = 600 * 1000L;
    private long refreshTokenValiditySeconds = 1800L;

    // AES-256 SecretKey를 생성하는 메서드 (Base64 디코딩)
    private SecretKey getAESKey() {
        byte[] decodedKey = Base64.getDecoder().decode(AES_256_GCM_KEY);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String generateAccessToken(String id, String role) throws Exception {
        return generateJWT(id, role, accessTokenValiditySeconds);
    }

    public String generateRefreshToken(String id, String role) throws Exception {
        return generateJWT(id, role, refreshTokenValiditySeconds);
    }

    private String generateJWT(String id, String role, long TokenValiditySeconds) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(id)
                .issueTime(new Date())
                .claim("id", id)
                .claim("role", role)
                .expirationTime(new Date(System.currentTimeMillis() + TokenValiditySeconds)) // 10분 유효
                .build();

        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);

        EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);

        SecretKey secretKey = getAESKey();
        DirectEncrypter encrypter = new DirectEncrypter(secretKey.getEncoded());

        encryptedJWT.encrypt(encrypter);

        return encryptedJWT.serialize();
    }

    // JWT 복호화 메서드
    private String decryptJWT(String encryptedJWT) throws Exception {
        // AES-256 SecretKey 불러오기
        SecretKey secretKey = getAESKey();

        // 암호화된 JWT를 파싱
        EncryptedJWT jwt = EncryptedJWT.parse(encryptedJWT);

        // 비밀 키로 복호화
        DirectDecrypter decrypter = new DirectDecrypter(secretKey.getEncoded());
        jwt.decrypt(decrypter);

        // 복호화된 JWT의 클레임을 Map 형태로 변환
        Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

        // ObjectMapper를 사용해 Map을 JSON 문자열로 변환
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(claims);
    }

    public boolean validateJWT(String encryptedJWT) throws Exception {
        // 1. JWT 복호화
        String decryptedJWT = decryptJWT(encryptedJWT);

        // 2. 복호화된 JWT의 클레임 추출
        EncryptedJWT jwt = EncryptedJWT.parse(encryptedJWT);
        Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();

        // 3. 토큰의 유효기간 검증
        if (expirationTime.before(new Date())) {
            log.warn("Token has expired");
            return false;  // 토큰이 만료되었으면 false 반환
        }

        // 4. 추가 검증이 필요한 경우 여기서 처리 (예: 서명 검증, issuer 검증 등)

        return true;  // 유효한 경우 true 반환
    }
}