package com.webtoon.infrastructure.webtoon;

import com.webtoon.domain.entity.Webtoon;
import org.springframework.data.domain.Page;

public interface WebtoonService {
    Page<Webtoon> getWebtoonPage(Integer page);
}
