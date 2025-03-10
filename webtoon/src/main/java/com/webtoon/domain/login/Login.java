package com.webtoon.domain.login;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class Login {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private String loginId;
        private String password;
    }
}
