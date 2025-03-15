package com.webtoon.domain.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialSource;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetWebtoonRecommend {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long contentId;
        private String title;
        private SerialSource serialSource;

        private Response(Webtoon webtoon) {
            this.id = webtoon.getId();
            this.contentId = webtoon.getContentId();
            this.title = webtoon.getTitle();
            this.serialSource = webtoon.getSerialSource();
        }

        public static Response toDto(Webtoon webtoon) {
            return new Response(webtoon);
        }
    }
}
