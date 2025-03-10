package com.webtoon.api;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.webtoon.GetWebtoonPage;
import com.webtoon.global.PageResponse;
import com.webtoon.infrastructure.webtoon.WebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebtoonFacade {

    private final WebtoonService webtoonService;

    public PageResponse<GetWebtoonPage.Response> getWebtoonPage(GetWebtoonPage.Request request) {
        Page<Webtoon> webtoonPage = webtoonService.getWebtoonPage(request.getPage());

        var responseList = webtoonPage.getContent().stream()
                .map(GetWebtoonPage.Response::create).toList();

        return new PageResponse<>(webtoonPage, responseList);
    }
}
