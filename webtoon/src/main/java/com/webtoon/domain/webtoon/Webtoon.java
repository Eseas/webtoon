package com.webtoon.domain.webtoon;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public abstract class Webtoon {
    private Long id;
    private String title;
    private Map<String, String> author;
    private Integer ageLimit;
    private Long totalEpisodes;
    private String briefText;
    private String status;
    private String hashtags;
    private LocalDateTime createdDt;
    private String createdId;
    private LocalDateTime updatedDt;
    private String updatedId;

    protected Webtoon(Long id, String title, Map<String, String> author, Integer ageLimit, Long totalEpisodes, String briefText,
                      String status, String hashtags, LocalDateTime createdDt, String createdId, LocalDateTime updatedDt,
                      String updatedId) {
        this.id = id;
        this.title = title;

        this.author = author;
        this.ageLimit = ageLimit;
        this.totalEpisodes = totalEpisodes;
        this.briefText = briefText;
        this.status = status;
        this.hashtags = hashtags;
        this.createdDt = createdDt;
        this.createdId = createdId;
        this.updatedDt = updatedDt;
        this.updatedId = updatedId;
    }
}