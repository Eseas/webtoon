package com.webtoon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 권장)
                .csrf(csrf -> csrf.disable())

                // 세션을 사용하지 않음 (JWT 사용을 위해 STATELESS 설정)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 권한 설정
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").authenticated()  // /admin 경로는 인증 필요
                        .anyRequest().permitAll()  // 나머지 경로는 허용
                )

                // HTTPS 강제
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure()  // 모든 요청에 대해 HTTPS 강제
                );

                // JWT 필터 추가 (필요 시 추가)
//                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new ForwardAuthenticationFailureHandler("/signin");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 설정
    }

    /**
     * /user 경로는 인증이 필요하도록 설정하려면 아래 라인을 추가할 수 있습니다.
     * .requestMatchers("/user/**").authenticated()
     */
}
