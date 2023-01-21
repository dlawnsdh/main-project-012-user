package com.example.mainproject012.dto.responose;

public record MemberSearchResponse(
        Long id,
        String nickname,
        String profileUrl,
        String memo,
        Long followers
) {
    public static MemberSearchResponse from(MemberResponse response) {
        return new MemberSearchResponse(
                response.id(),
                response.nickname(),
                response.profileUrl(),
                response.memo(),
                response.followers()
        );
    }

}
