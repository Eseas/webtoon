package com.webtoon.service.Webtoon;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import com.webtoon.dto.Webtoon.WebtoonDetailDto;
import com.webtoon.repository.jpa.KakaoWebtoonRepository;
import com.webtoon.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class WebtoonServiceImpl implements WebtoonService {
    private final KakaoWebtoonRepository kakaoWebtoonRepository;

    private final RedisUtils redisUtils;

    @Override
    public KakaoWebtoon findById(WebtoonDetailDto webtoonDetailDto) {
        switch (webtoonDetailDto.getPlatform().toLowerCase()) {
            case "naver":
                return null;
            case "kakao":
                return kakaoWebtoonRepository.findById(webtoonDetailDto.getId()).orElse(null);
        }
        return null;
    }

    @Override
    public ArrayList<KakaoWebtoon> findPage(int userlimit, int page) {
        int limit = (page == 1) ? 40 : userlimit;  // 첫 번째 페이지는 40개, 그 이후 페이지는 파라미터로 받은 값 사용
        int offset = (page == 1) ? 0 : 40 + (page - 2) * limit;
        // log.info("page = {}, limit = {}, offset = {}", page, limit, offset);
        return kakaoWebtoonRepository.findLimitOffset(limit, offset);
    }
}
