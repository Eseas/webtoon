package com.webtoon.dto.Login.SocialLogin;

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
}
