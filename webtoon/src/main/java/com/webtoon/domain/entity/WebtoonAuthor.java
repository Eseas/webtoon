package com.webtoon.domain.entity;

import com.webtoon.domain.entity.constant.AuthorRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "webtoon_author")
public class WebtoonAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Webtoon webtoon;

    @ManyToOne(fetch = FetchType.LAZY)
    private Author author;

    private AuthorRole authorRole;
}
