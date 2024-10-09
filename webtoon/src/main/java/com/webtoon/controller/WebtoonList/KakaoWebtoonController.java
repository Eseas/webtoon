package com.webtoon.controller.WebtoonList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoWebtoonController {

    @GetMapping("/webtoon-list")
    public String getWebtoonList(Model model) {
        return "webtoon-list";
    }
}
