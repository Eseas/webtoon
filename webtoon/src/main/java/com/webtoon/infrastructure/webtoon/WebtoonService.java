package com.webtoon.infrastructure.webtoon;

import com.webtoon.domain.entity.Webtoon;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WebtoonService {
    Webtoon getWebtoon(Long id);
    Page<Webtoon> getWebtoonPage(Integer page);
    List<Webtoon> getWebtoonRecommend(Long id);
}
