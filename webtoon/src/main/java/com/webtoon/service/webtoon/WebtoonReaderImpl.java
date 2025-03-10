package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.infrastructure.webtoon.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebtoonReaderImpl implements WebtoonReader {

    private final WebtoonRepository webtoonRepository;

    @Override
    public Page<Webtoon> readPage(Pageable pageable) {
        return webtoonRepository.findAll(pageable);
    }
}
