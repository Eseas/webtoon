package com.webtoon.service.webtoon;

import com.webtoon.domain.entity.Webtoon;
import com.webtoon.domain.entity.constant.SerialCycle;
import com.webtoon.domain.entity.constant.SerialSource;
import com.webtoon.infrastructure.webtoon.WebtoonService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebtoonServiceImpl implements WebtoonService {

    private final WebtoonReader webtoonReader;

    @Override
    public Webtoon getWebtoon(Long id) {
        return webtoonReader.read(id);
    }

    @Override
    public Page<Webtoon> getWebtoonPage(SerialSource serialSource, SerialCycle serialCycle, Integer page, Integer offset) {
        Pageable pageable = PageRequest.of(page, offset);

        return webtoonReader.readPage(serialSource, serialCycle, pageable);
    }

    @Override
    public List<Webtoon> getWebtoonRecommend(Long id) {
        List<Webtoon> webtoons = webtoonReader.readAll();

        // 조회한 웹툰 데이터가 없으면 빈 리스트 반환
        if (webtoons.isEmpty()) return Collections.emptyList();

        // 추천을 위한 기준이 되는 웹툰 찾기
        Webtoon targetWebtoon = webtoonReader.read(id);

        // 웹툰의 장르 및 설명을 활용하여 추천
        Map<Webtoon, Double> similarityScores = new HashMap<>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        for (Webtoon webtoon : webtoons) {
            if (!webtoon.getId().equals(targetWebtoon.getId())) {
                double similarity = calculateSimilarity(targetWebtoon, webtoon, cosineSimilarity);
                similarityScores.put(webtoon, similarity);
            }
        }

        // 유사도가 높은 순으로 정렬하여 상위 5개 추천
        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Webtoon, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 코사인 유사도를 활용한 웹툰 유사도 계산
    private double calculateSimilarity(Webtoon webtoon1, Webtoon webtoon2, CosineSimilarity cosineSimilarity) {
        String text1 = (webtoon1.getGenre() + " " + webtoon1.getDescription()).toLowerCase();
        String text2 = (webtoon2.getGenre() + " " + webtoon2.getDescription()).toLowerCase();

        // 단어별 빈도수를 계산하는 벡터화
        Map<CharSequence, Integer> vector1 = getWordFrequency(text1);
        Map<CharSequence, Integer> vector2 = getWordFrequency(text2);

        return cosineSimilarity.cosineSimilarity(vector1, vector2);
    }

    // 텍스트를 단어 빈도 벡터로 변환
    private Map<CharSequence, Integer> getWordFrequency(String text) {
        Map<CharSequence, Integer> frequencyMap = new HashMap<>();
        String[] words = text.split("\\s+"); // 공백 기준으로 단어 분리

        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }

        return frequencyMap;
    }
}
