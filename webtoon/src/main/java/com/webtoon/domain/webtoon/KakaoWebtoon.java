package com.webtoon.domain.webtoon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "kakao_webtoon", schema = "webtoon")
public class KakaoWebtoon extends Webtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "total_episodes", nullable = false)
    private Long totalEpisodes;

    @Column(length = 5, nullable = false)
    private String status;

    @Column(name = "upload_cycle", nullable = false)
    private Integer uploadCycle;

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @Column(name = "brief_text", columnDefinition = "TEXT", nullable = false)
    private String briefText;

    @Column(columnDefinition = "json")
    private String hashtags;

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

    @Builder
    public KakaoWebtoon(Long id, String title, String author, Long totalEpisodes, String status, Integer uploadCycle,
                        Integer ageLimit, String briefText, String hashtags, LocalDateTime createdDt, String createdId,
                        LocalDateTime updatedDt, String updatedId) {
        super(id, title, author, ageLimit, totalEpisodes, briefText, status, hashtags, createdDt, createdId, updatedDt, updatedId);
        this.uploadCycle = uploadCycle;
    }
}