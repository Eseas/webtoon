package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WebtoonReader {
    Page<Webtoon> readPage(Pageable pageable);
}
