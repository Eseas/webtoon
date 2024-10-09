package com.webtoon.dto.Login.SocialLogin;

import com.fasterxml.jackson.databind.JsonNode;

public class KakaoLoginAPIProfileResponse implements LoginAPIProfileResponse {
    private JsonNode jsonNode;

    public KakaoLoginAPIProfileResponse(JsonNode jsonNode) {
        this.jsonNode = jsonNode.get("kakao_account");
    }


    // 만약 선택 동의 사항이 있다면, 선택 동의 사항을 체크하는 로직을 추가해야 한다.
    @Override
    public String getLoginId() {
        if(jsonNode.get("has_email").asBoolean()) {
            return jsonNode.get("email").asText();
        }
        return "";
    }

    @Override
    public String getName() {
        return jsonNode.get("name").asText();
    }

    public String getBirth() {
        if(jsonNode.get("has_birthyear").asBoolean()) {
            return jsonNode.get("birthyear").asText();
        }
        return "";
    }

    public String getGender() {
        if(jsonNode.get("has_gender").asBoolean()) {
            String gender = jsonNode.get("gender").asText();
            return gender.equals("male") ? "M" : "F";
        }
        return "";
    }

    public String getAgeRange() {
        if(jsonNode.get("has_age_range").asBoolean()) {
            return jsonNode.get("age_range").asText();
        }
        return "";
    }
}
