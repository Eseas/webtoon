package com.webtoon.infrastructure.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    @Query("""
    SELECT w
    FROM Webtoon w
    WHERE (:serialSource IS NULL OR w.serialSource = :serialSource)
      AND (:serialCycle = '' OR w.serialCycle LIKE :serialCycle)
    """)
    Page<Webtoon> findPageBySerialSourceAndSerialCycle(
            @Param("serialSource") SerialSource serialSource,
            @Param("serialCycle") String serialCycle,
            Pageable pageable);
}
