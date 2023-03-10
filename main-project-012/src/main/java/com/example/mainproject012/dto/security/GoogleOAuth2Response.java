package com.example.mainproject012.dto.security;

import java.util.Map;

@SuppressWarnings("unchecked")
public record GoogleOAuth2Response(
        String email,
        String nickname,
        String profileUrl
) {
    public static GoogleOAuth2Response from(Map<String, Object> attributes) {
        return new GoogleOAuth2Response(
                String.valueOf(attributes.get("email")),
                String.valueOf(attributes.get("name")),
                String.valueOf(attributes.get("picture"))
        );
    }

    public MemberPrincipal toPrincipal() {
        return MemberPrincipal.of(
                "",
                email,
                nickname,
                null,
                null,
                profileUrl,
                null
        );
    }

}
