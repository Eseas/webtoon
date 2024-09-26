package com.webtoon.utils.JWT;

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

import static org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.*;

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

    public String generateAccessToken(String id, String pwd) throws Exception {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(id)
                .issueTime(new Date())
                .claim("id", id)
                .claim("pwd", pwd)
                .expirationTime(new Date(System.currentTimeMillis() + accessTokenValiditySeconds)) // 10분 유효
                .build();

        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);

        EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);

        SecretKey secretKey = getAESKey();
        DirectEncrypter encrypter = new DirectEncrypter(secretKey.getEncoded());

        encryptedJWT.encrypt(encrypter);

        return encryptedJWT.serialize();
    }

    // JWT 복호화 메서드
    public String decryptJWT(String encryptedJWT) throws Exception {
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
}