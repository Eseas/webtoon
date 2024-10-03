package com.webtoon.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class NaverLoginAPIProfileResponse implements LoginAPIProfileResponse {
    private JsonNode jsonNode;

    public NaverLoginAPIProfileResponse(JsonNode jsonNode) {
        this.jsonNode = jsonNode.get("response");
    }

    @Override
    public String getId() {
        return jsonNode.get("id").asText();
    }

    @Override
    public String getLoginId() {
        return jsonNode.get("login_id").asText();
    }

    @Override
    public String getName() {
        return jsonNode.get("name").asText();
    }

    @Override
    public String getProfile_image() {
        return jsonNode.get("profile_image").asText();
    }

    public String getGender() {
        return jsonNode.get("gender").asText();
    }

    public String getAge() {
        return jsonNode.get("age").asText();
    }

    public String getBirthYear() {
        return jsonNode.get("birth_year").asText();
    }

    @Override
    public String toString() {
        return "NaverLoginAPIProfileResponse{" +
                "userInfo=" + jsonNode +
                '}';
    }
}
