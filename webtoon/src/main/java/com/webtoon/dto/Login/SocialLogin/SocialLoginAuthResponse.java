package com.webtoon.dto.Login.SocialLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SocialLoginAuthResponse {
    private String code;
    private String error;
    private String error_description;
    private String state;
}
