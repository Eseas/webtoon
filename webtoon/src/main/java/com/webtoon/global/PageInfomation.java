package com.webtoon.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PageInfomation {
     GET_WEBTOON_SIZE(10);

    private final Integer number;
}
