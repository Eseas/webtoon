package com.webtoon.repository.jpa;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoWebtoonRepository extends JpaRepository<KakaoWebtoon, Long> {

}
