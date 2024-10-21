package com.webtoon.controller.Webtoon;

import com.webtoon.domain.webtoon.Webtoon;
import com.webtoon.dto.Webtoon.WebtoonDetailDto;
import com.webtoon.service.Webtoon.WebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebtoonController {

    private final WebtoonService webtoonService;

    @GetMapping("/webtoon/detail")
    public String getWebtoonDetail(

    ) throws Exception {
        return "webtoon-detail";
    }
}
