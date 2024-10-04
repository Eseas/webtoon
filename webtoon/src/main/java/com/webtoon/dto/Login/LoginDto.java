package com.webtoon.dto.Login;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class LoginDto {
    private String id;
    private String pwd;

}
