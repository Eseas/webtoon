package com.webtoon.repository.jpa;

import com.webtoon.domain.webtoon.NaverWebtoon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NaverWebtoonRepository extends JpaRepository<NaverWebtoon, Long> {

}
