package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.global.PageInfomation;
import com.webtoon.infrastructure.webtoon.WebtoonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.webtoon.global.PageInfomation.GET_WEBTOON_SIZE;

@Service
@RequiredArgsConstructor
public class WebtoonServiceImpl implements WebtoonService {

    private final WebtoonReader webtoonReader;

    @Override
    public Page<Webtoon> getWebtoonPage(Integer page) {
        Pageable pageable = PageRequest.of(page, GET_WEBTOON_SIZE.getNumber());

        return webtoonReader.readPage(pageable);
    }
}
