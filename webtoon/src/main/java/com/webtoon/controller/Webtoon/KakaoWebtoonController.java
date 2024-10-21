package com.webtoon.controller.Webtoon;

import com.webtoon.domain.webtoon.Webtoon;
import com.webtoon.service.Webtoon.WebtoonService;
import com.webtoon.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoWebtoonController {

    private final RedisUtils redisUtils;

    private final WebtoonService webtoonService;

    @GetMapping("/webtoon-list")
    public String getWebtoonList(
    ) throws Exception {
        return "webtoon-list";
    }

    @GetMapping("/api/webtoons")
    public ResponseEntity<ArrayList<Webtoon>> getWebtoons(
        @RequestParam(name="limit") Integer limit,
        @RequestParam(name="page") Integer page
    ) throws Exception {
        if(limit == null || page == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            ArrayList<Webtoon> webtoons = new ArrayList<>();
            webtoons.addAll(webtoonService.findPage(limit, page));
            return new ResponseEntity<>(webtoons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
