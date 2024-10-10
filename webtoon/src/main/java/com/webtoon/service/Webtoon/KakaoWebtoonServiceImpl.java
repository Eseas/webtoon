package com.webtoon.service.Webtoon;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import com.webtoon.repository.jpa.KakaoWebtoonRepository;
import com.webtoon.utils.RedisUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KakaoWebtoonServiceImpl implements KakaoWebtoonService {
    private final KakaoWebtoonRepository kakaoWebtoonRepository;

    private final RedisUtils redisUtils;

    @Override
    public KakaoWebtoon findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<KakaoWebtoon> findPage(Integer limit, Integer offset) {
        return kakaoWebtoonRepository.findLimitOffset(limit, offset);
    }
}
