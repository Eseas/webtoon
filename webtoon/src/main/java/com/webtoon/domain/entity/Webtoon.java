package com.webtoon.domain.entity;

import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import com.webtoon.domain.entity.constant.SerialStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    private String interestCount;

    private String viewCount;

    private String commentCount;

    private LocalDate lastUploadDate;

    private SerialStatus serialStatus;

    private String serialCycle;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Transient getter: DB에 저장된 문자열을 List<SerialCycle>로 변환하여 반환
    @Transient
    public List<SerialCycle> getSerialCycleList() {
        if (serialCycle == null || serialCycle.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(serialCycle.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .map(i -> SerialCycle.values()[i])
                .collect(Collectors.toList());
    }
}
