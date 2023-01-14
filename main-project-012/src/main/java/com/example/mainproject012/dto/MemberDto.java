package com.example.mainproject012.dto;

import com.example.mainproject012.domain.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public record MemberDto(
        Long memberId,
        String email,
        String nickname,
        String birthday,
        String profileUrl,
        Integer gender,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static MemberDto of(Long memberId, String email, String nickname, String birthday, String profileUrl, Integer gender, String memo, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new MemberDto(memberId, email, nickname, birthday, profileUrl, gender, memo, createdAt, modifiedAt);
    }

    public static MemberDto of(String email, String nickname, String birthday, String profileUrl, Integer gender, String memo) {
        return new MemberDto(null, email, nickname, birthday, profileUrl, gender, memo,null, null);
    }

    public static MemberDto of(String email, String nickname, String profileUrl) {
        return new MemberDto(null, email, nickname, null, profileUrl, null, null, null, null);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getMemberId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getBirthday(),
                entity.getProfileUrl(),
                entity.getGender(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    public Member toEntity() {
        return Member.of(
                email,
                nickname,
                birthday,
                profileUrl,
                gender,
                memo
        );
    }

}