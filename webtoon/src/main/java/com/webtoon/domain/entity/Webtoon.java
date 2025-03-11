package com.webtoon.domain.entity;

import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import com.webtoon.domain.entity.constant.SerialStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "webtoon")
public class Webtoon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId;

    private String title;

    @OneToMany(mappedBy = "webtoon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebtoonAuthor> authors;

    private SerialSource serialSource;

    private Integer totalEpisodeCount;

    private String genre;

    private Integer ageLimit;

    private String viewCount;

    private String commentCount;

    private LocalDate lastUploadDate;

    private SerialStatus serialStatus;

    private SerialCycle serialCycle;

    @Column(columnDefinition = "TEXT")
    private String description;
}
