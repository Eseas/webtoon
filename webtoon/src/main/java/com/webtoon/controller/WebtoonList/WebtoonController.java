package com.webtoon.controller.WebtoonList;

import com.webtoon.domain.webtoon.Webtoon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebtoonController {

    @GetMapping("/webtoon/detail")
    public ResponseEntity<Webtoon> getWebtoonDetail(
            //@ModelAttribute WebtoonDetailDto webtoonDetailDto
    ) throws Exception {

        return null;
    }

}
