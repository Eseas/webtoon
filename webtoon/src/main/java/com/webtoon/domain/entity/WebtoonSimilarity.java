package com.webtoon.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "webtoon_similarity")
public class WebtoonSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "webtoon_id1")
    private Webtoon webtoonId1;

    @ManyToOne
    @JoinColumn(name = "webtoon_id2")
    private Webtoon webtoonId2;

    private Float similarity;
}
