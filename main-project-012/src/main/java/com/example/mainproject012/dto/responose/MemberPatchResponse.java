package com.example.mainproject012.dto.responose;

public record MemberPatchResponse(
        String nickname,
        String profileUrl,
        String memo
) {
    public static MemberPatchResponse from(MemberResponse response) {
        return new MemberPatchResponse(
                response.nickname(),
                response.profileUrl(),
                response.memo()
        );
    }

}
