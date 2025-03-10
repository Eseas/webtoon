package com.webtoon.controller;

import com.webtoon.api.WebtoonFacade;
import com.webtoon.domain.webtoon.GetWebtoonPage;
import com.webtoon.global.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WebtoonController {

    private final WebtoonFacade webtoonFacade;

    @GetMapping("/webtoons")
    public ResponseEntity<PageResponse<GetWebtoonPage.Response>> getWebtoonPage(
            GetWebtoonPage.Request request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(webtoonFacade.getWebtoonPage(request));
    }
}
