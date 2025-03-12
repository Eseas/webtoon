package com.webtoon.controller;

import com.webtoon.api.WebtoonFacade;
import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.webtoon.GetWebtoonPage;
import com.webtoon.domain.webtoon.GetWebtoonRecommend;
import com.webtoon.global.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WebtoonController {

    private final WebtoonFacade webtoonFacade;

    @GetMapping("/webtoons")
    public ResponseEntity<PageResponse<GetWebtoonPage.Response>> getWebtoonPage(
            @RequestParam Integer page
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(webtoonFacade.getWebtoonPage(page));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<GetWebtoonRecommend.Response>> recommendWebtoons(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(webtoonFacade.getWebtoonRecommend(id));
    }
}
