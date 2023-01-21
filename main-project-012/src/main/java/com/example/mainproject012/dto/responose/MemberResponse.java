package com.example.mainproject012.dto.responose;

import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.MemberDto;

public record MemberResponse(
        Long id,
        String email,
        String nickname,
        String birthday,
        String profileUrl,
        Integer gender,
        String memo,
        Long followers,
        Long followings
) {
    public static MemberResponse from(MemberDto dto) {
        return new MemberResponse(
                dto.memberId(),
                dto.email(),
                dto.nickname(),
                dto.birthday(),
                dto.profileUrl(),
                dto.gender(),
                dto.memo(),
                null,
                null
        );
    }

    public static MemberResponse from(Member entity) {
        return new MemberResponse(
                entity.getMemberId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getBirthday(),
                entity.getProfileUrl(),
                entity.getGender(),
                entity.getMemo(),
                entity.getFollowers(),
                entity.getFollowings()
        );
    }

}
