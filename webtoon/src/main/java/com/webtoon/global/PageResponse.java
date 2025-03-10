package com.webtoon.global;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {
    private Integer page;
    private Long totalElements;
    private Boolean isLast;
    private List<T> content;

    public PageResponse(Page<T> page) {
        this.page = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.isLast = page.isLast();
        this.content = page.getContent();
    }

    public PageResponse(Page page, List<T> content) {
        this.page = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.isLast = page.isLast();
        this.content = content;
    }
}
