package com.webtoon.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class GoogleLoginAPIProfileResponse implements LoginAPIProfileResponse {
    private JsonNode jsonNode;

    public GoogleLoginAPIProfileResponse(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    public String getId() {
        return jsonNode.get("sub").asText();
    }

    @Override
    public String getLoginId() {
        return jsonNode.get("email").asText();
    }

    @Override
    public String getName() {
        return jsonNode.get("name").asText();
    }

    @Override
    public String getProfile_image() {
        return jsonNode.get("picture").asText();
    }
}