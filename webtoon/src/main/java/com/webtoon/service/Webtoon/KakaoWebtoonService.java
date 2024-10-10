package com.webtoon.service.Webtoon;

import com.webtoon.domain.webtoon.KakaoWebtoon;

import java.util.ArrayList;

public interface KakaoWebtoonService {
    KakaoWebtoon findById(Long id);
    ArrayList<KakaoWebtoon> findPage(Integer limit, Integer offset);
}
