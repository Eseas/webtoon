package com.webtoon.controller.Webtoon;

import com.webtoon.domain.webtoon.Webtoon;
import com.webtoon.dto.Webtoon.WebtoonDetailDto;
import com.webtoon.service.Webtoon.WebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class WebtoonRestController {
    private final WebtoonService webtoonService;

    @GetMapping("/api/webtoon")
    public ResponseEntity<Webtoon> getWebtoonDetail(
            @ModelAttribute WebtoonDetailDto webtoonDetailDto
    ) throws Exception {
        Webtoon findWebtoon = webtoonService.findById(webtoonDetailDto);

        if (findWebtoon == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(findWebtoon, HttpStatus.OK);
    }
}
