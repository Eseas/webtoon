package com.webtoon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin((formLogin) ->
                formLogin
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/signin")
                        .failureUrl("/signin?failed")
                        .loginProcessingUrl("/signin")
        );

        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").authenticated() // /admin 경로는 인증 필요
                        .anyRequest().permitAll() // 나머지 경로는 허용
                )
                .httpBasic(withDefaults()) // HTTP 기본 인증 사용
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure() // 모든 요청에 대해 HTTPS 강제
                )
                .formLogin(login -> login.disable()); // 로그인 페이지를 비활성화하여 자동 리다이렉트 방지

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 설정
    }

    /**
     * 나중에 특정 URL에 대해 인증을 요구할 수 있도록 아래 코드를 사용하세요:
     * 예: /user 경로는 인증이 필요하도록 설정하려면 아래 라인을 추가할 수 있습니다.
     * .requestMatchers("/user/**").authenticated()
     */
}
