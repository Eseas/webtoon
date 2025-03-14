package com.webtoon.domain.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerialCycle {
    MON(0),
    TUE(1),
    WED(2),
    THU(3),
    FRI(4),
    SAT(5),
    SUN(6),
    DAY(7),
    TEN(8),
    FIN(9);

    private final Integer order;
}
