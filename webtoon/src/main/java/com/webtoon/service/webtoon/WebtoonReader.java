package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WebtoonReader {
    Webtoon read(Long id);
    Page<Webtoon> readPage(SerialSource serialSource, SerialCycle serialCycle, Pageable pageable);
    List<Webtoon> readAll();
}
