package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.exception.BusinessException;
import com.webtoon.infrastructure.webtoon.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebtoonReaderImpl implements WebtoonReader {

    private final WebtoonRepository webtoonRepository;

    @Override
    public Webtoon read(Long id) {
        return webtoonRepository.findById(id).orElseThrow(BusinessException::new);
    }

    @Override
    public Page<Webtoon> readPage(Pageable pageable) {
        return webtoonRepository.findAll(pageable);
    }

    @Override
    public List<Webtoon> readAll() {
        return webtoonRepository.findAll();
    }
}
