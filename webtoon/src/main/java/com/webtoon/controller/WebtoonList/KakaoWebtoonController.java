package com.webtoon.controller.WebtoonList;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import com.webtoon.domain.webtoon.Webtoon;
import com.webtoon.service.Webtoon.KakaoWebtoonService;
import com.webtoon.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoWebtoonController {

    private final RedisUtils redisUtils;

    private final KakaoWebtoonService kakaoWebtoonService;

    @GetMapping("/webtoon-list")
    public String getWebtoonList(
    ) throws Exception {
        return "webtoon-list";
    }

    @GetMapping("/api/webtoons")
    public ArrayList<? extends Webtoon> getWebtoons(
        @RequestParam(name="limit") Integer limit,
        @RequestParam(name="page") Integer page
    ) throws Exception {
        ArrayList<Webtoon> webtoons = new ArrayList<>();
        int offset = (page - 1) * 20 + (page == 1 ? 0 : 40);
        webtoons.addAll(kakaoWebtoonService.findPage(limit, offset));
        return webtoons;
    }
}
