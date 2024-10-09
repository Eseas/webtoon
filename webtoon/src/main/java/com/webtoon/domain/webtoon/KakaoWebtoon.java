package com.webtoon.domain.webtoon;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "kakao_webtoon", schema = "webtoon")
public class KakaoWebtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String author; // JSON field can be handled with a converter or a specific type, depending on use case.

    @Column(name = "total_episodes", nullable = false)
    private Long totalEpisodes;

    @Column(length = 5, nullable = false)
    private String status;

    @Column(name = "upload_cycle", nullable = false)
    private Integer uploadCycle;

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @Column(name = "biref_text", columnDefinition = "TEXT", nullable = false)
    private String briefText;

    @Column(columnDefinition = "json")
    private String hashtags; // JSON field can be handled with a converter or a specific type, depending on use case.

    @CreationTimestamp
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "created_id", length = 50, nullable = false)
    private String createdId;

    @UpdateTimestamp
    @Column(name = "updated_dt", nullable = false)
    private LocalDateTime updatedDt;

    @Column(name = "updated_id", length = 50, nullable = false)
    private String updatedId;
}
