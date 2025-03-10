package com.webtoon.domain.webtoon;

import com.webtoon.domain.entity.SerialSource;
import com.webtoon.domain.entity.Webtoon;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetWebtoonPage {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private SerialSource serialSource;
        private Integer page;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String author;

        private Response(Webtoon webtoon) {
            this.id = webtoon.getId();
            this.title = webtoon.getTitle();
            this.author = webtoon.getAuthor().getName();
        }

        public static Response create(Webtoon webtoon) {
            return new Response(webtoon);
        }
    }
}
