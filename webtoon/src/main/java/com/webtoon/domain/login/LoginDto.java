package com.webtoon.domain.login;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class LoginDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private String loginId;
        private String password;
    }
}
