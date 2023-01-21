package com.example.mainproject012.dto.responose;

import com.example.mainproject012.dto.MemberDto;

public record FollowResponseWithInfo(
        String nickname,
        String profileUrl
) {
    public static FollowResponseWithInfo of(String nickname, String profileUrl) {
        return new FollowResponseWithInfo(nickname, profileUrl);
    }

    public static FollowResponseWithInfo from(MemberDto dto) {
        return new FollowResponseWithInfo(dto.nickname(), dto.profileUrl());
    }

}
