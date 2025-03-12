package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WebtoonReader {
    Webtoon read(Long id);
    Page<Webtoon> readPage(Pageable pageable);
    List<Webtoon> readAll();
}
