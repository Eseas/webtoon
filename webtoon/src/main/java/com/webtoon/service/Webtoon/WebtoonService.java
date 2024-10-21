package com.webtoon.service.Webtoon;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import com.webtoon.dto.Webtoon.WebtoonDetailDto;

import java.util.ArrayList;

public interface WebtoonService {
    KakaoWebtoon findById(WebtoonDetailDto webtoonDetailDto);
    ArrayList<KakaoWebtoon> findPage(int userlimit, int page);
}
