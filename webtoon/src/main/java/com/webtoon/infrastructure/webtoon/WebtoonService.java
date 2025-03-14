package com.webtoon.infrastructure.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WebtoonService {
    Webtoon getWebtoon(Long id);
    Page<Webtoon> getWebtoonPage(SerialSource serialSource, SerialCycle serialCycle, Integer page, Integer offset);
    List<Webtoon> getWebtoonRecommend(Long id);
}
