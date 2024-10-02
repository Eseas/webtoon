package com.webtoon.dto;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class LoginDto {
    private String id;
    private String pwd;

}
