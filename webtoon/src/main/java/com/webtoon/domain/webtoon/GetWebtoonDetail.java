package com.webtoon.domain.webtoon;

import com.webtoon.domain.entity.Author;
import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWebtoonDetail {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long contentId;
        private String title;
        private Map<String, String> authorList;
        private Integer totalEpisodes;
        private Integer ageLimit;
        private String serialStatus;
        private SerialSource serialSource;
        private List<SerialCycle> uploadCycle;
        private String description;

        private Response(Webtoon webtoon) {
            Map<String, String> authorList = new HashMap<>();

            this.id = webtoon.getId();
            this.contentId = webtoon.getContentId();
            this.title = webtoon.getTitle();
            webtoon.getAuthors().forEach(author -> {
                authorList.put(author.getAuthor().getName(), author.getAuthorRole().name());
            });
            this.authorList = authorList;
            this.totalEpisodes = webtoon.getTotalEpisodeCount();
            this.ageLimit = webtoon.getAgeLimit();
            this.serialStatus = webtoon.getSerialStatus().name();
            this.serialSource = webtoon.getSerialSource();
            this.uploadCycle = webtoon.getSerialCycleList();
            this.description = webtoon.getDescription();
        }

        public static Response toDto(Webtoon webtoon) {
            return new Response(webtoon);
        }
    }
}
