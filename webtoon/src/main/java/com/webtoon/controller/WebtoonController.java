package com.webtoon.controller;

import com.webtoon.api.WebtoonFacade;
import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import com.webtoon.domain.webtoon.GetWebtoonDetail;
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
            @RequestParam(required = false) SerialSource publisher,
            @RequestParam(required = false) SerialCycle day,
            @RequestParam Integer page,
            @RequestParam Integer offset
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(webtoonFacade.getWebtoonPage(publisher, day, page, offset));
    }

    @GetMapping("/webtoons/detail")
    public ResponseEntity<GetWebtoonDetail.Response> getWebtoonDetail(
            @RequestParam Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(webtoonFacade.getWebtoonDetail(id));
    }

    @GetMapping("/webtoons/recommend")
    public ResponseEntity<List<GetWebtoonRecommend.Response>> recommendWebtoons(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(webtoonFacade.getWebtoonRecommend(id));
    }
}
