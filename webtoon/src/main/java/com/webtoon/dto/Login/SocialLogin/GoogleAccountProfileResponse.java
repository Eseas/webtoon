package com.webtoon.dto.Login.SocialLogin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GoogleAccountProfileResponse {
    private String id;
    private String email;
    private boolean verified_email;
    private String given_name;
    private String family_name;
}
