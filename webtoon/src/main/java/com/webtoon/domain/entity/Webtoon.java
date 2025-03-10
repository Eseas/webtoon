package com.webtoon.domain.entity;

import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "webtoon")
public class Webtoon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Author author;

    private SerialSource serialSource;

    private Integer totalEpisodeCount;

    private String genre;

    private Integer ageLimit;

    private Long viewCount;

    private Long commentCount;

    private LocalDate lastUploadDate;

    private SerialStatus serialStatus;

    private SerialCycle serialCycle;

    private String description;
}
