package com.webtoon.domain.webtoon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "naver_webtoon", schema = "webtoon")
public class NaverWebtoon extends Webtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> author;

    @Column(name = "age_limit")
    private Integer ageLimit;

    @Column(name = "total_episodes")
    private Long totalEpisodes;

    @Column(name = "brief_text", columnDefinition = "TEXT")
    private String briefText;

    @Column(name = "interest_count", columnDefinition = "bigint default 0")
    private Long interestCount;

    @Column(length = 5)
    private String status;

    @Column(columnDefinition = "json")
    private String hashtags;

    @CreationTimestamp
    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @Column(name = "created_id", length = 20)
    private String createdId;

    @UpdateTimestamp
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Column(name = "updated_id", length = 20)
    private String updatedId;

    @Column(name = "upload_day", length = 20)
    private String uploadDay;

    @Builder
    public NaverWebtoon(Long id, String title, Map<String, String> author, Integer ageLimit, Long totalEpisodes, String briefText,
                        Long interestCount, String status, String hashtags, LocalDateTime createdDt, String createdId,
                        LocalDateTime updatedDt, String updatedId, String uploadDay) {
        super(id, title, author, ageLimit, totalEpisodes, briefText, status, hashtags, createdDt, createdId, updatedDt, updatedId);
        this.interestCount = interestCount;
        this.uploadDay = uploadDay;
    }
}