package com.webtoon.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {
    private String id;
    private String pwd;
//    private String username;
//    private boolean remember_id;

}
