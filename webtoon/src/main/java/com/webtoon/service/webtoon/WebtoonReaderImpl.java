package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
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
    public Page<Webtoon> readPage(SerialSource serialSource, SerialCycle serialCycle, Pageable pageable) {
        String cycleParam = serialCycle == null ? "" : "%" + serialCycle.getOrder() + "%";

        return webtoonRepository.findPageBySerialSourceAndSerialCycle(serialSource, cycleParam, pageable);
    }

    @Override
    public List<Webtoon> readAll() {
        return webtoonRepository.findAll();
    }
}
