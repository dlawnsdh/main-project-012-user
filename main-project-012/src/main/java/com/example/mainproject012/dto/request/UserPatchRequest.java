package com.example.mainproject012.dto.request;

public record UserPatchRequest(
        String nickname,
        String profileUrl,
        String memo
) {
    public static UserPatchRequest of(String nickname, String profileUrl, String memo) {
        return new UserPatchRequest(nickname, profileUrl, memo);
    }

}
