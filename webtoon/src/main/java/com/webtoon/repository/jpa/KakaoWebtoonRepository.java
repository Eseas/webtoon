package com.webtoon.repository.jpa;

import com.webtoon.domain.webtoon.KakaoWebtoon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface KakaoWebtoonRepository extends JpaRepository<KakaoWebtoon, Long> {
    Optional<KakaoWebtoon> findById(Long id);

    @Query("SELECT k_w FROM KakaoWebtoon k_w ORDER BY k_w.id ASC LIMIT :limit OFFSET :offset")
    ArrayList<KakaoWebtoon> findLimitOffset(@Param("limit") Integer limit,
                                            @Param("offset") Integer offset
                                 );
}
