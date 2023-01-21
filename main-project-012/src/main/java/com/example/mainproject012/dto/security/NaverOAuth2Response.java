package com.example.mainproject012.dto.security;

import java.util.Map;


@SuppressWarnings("unchecked")
public record NaverOAuth2Response(
        String email,
        String nickname,
        String photoUrl,
        String birthday,
        String gender
) {
    public static NaverOAuth2Response from(Map<String, Object> attributes) {
        return new NaverOAuth2Response(
                String.valueOf(attributes.get("email")),
                String.valueOf(attributes.get("name")),
                String.valueOf(attributes.get("photoUrl")),
                null,
                null
        );
    }

}

