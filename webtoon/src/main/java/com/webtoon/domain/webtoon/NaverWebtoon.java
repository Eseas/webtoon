package com.webtoon.domain.webtoon;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "naver_webtoon", schema = "webtoon")
public class NaverWebtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column
    private String author; // JSON field can be handled with a converter or a specific type, depending on use case.

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
    private String hashtags; // JSON field can be handled with a converter or a specific type, depending on use case.

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
}
