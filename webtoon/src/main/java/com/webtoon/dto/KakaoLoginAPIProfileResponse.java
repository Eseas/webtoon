package com.webtoon.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class KakaoLoginAPIProfileResponse implements LoginAPIProfileResponse {
    private JsonNode jsonNode;

    public KakaoLoginAPIProfileResponse(JsonNode jsonNode) {
        this.jsonNode = jsonNode.get("kakao_account");
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getLoginId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getProfile_image() {
        return "";
    }
}
