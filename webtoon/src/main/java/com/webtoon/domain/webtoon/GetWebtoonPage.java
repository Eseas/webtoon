package com.webtoon.domain.webtoon;

import com.webtoon.domain.entity.Author;
import com.webtoon.domain.entity.constant.SerialSource;
import com.webtoon.domain.entity.Webtoon;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetWebtoonPage {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private List<String> author;

        private Response(Webtoon webtoon) {
            this.id = webtoon.getContentId();
            this.title = webtoon.getTitle();
            this.author = webtoon.getAuthors().stream().map(authors -> authors.getAuthor().getName()).toList();
        }

        public static Response create(Webtoon webtoon) {
            return new Response(webtoon);
        }
    }
}
