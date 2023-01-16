package com.example.mainproject012.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
public class Member {
    @Id
    private Long memberId;

    private String email;

    private String nickname;

    private String birthday;

    private String profileUrl;

    private Integer gender;

    private String memo;

    @CreatedDate private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private Member(String email, String nickname, String birthday, String profileUrl, Integer gender, String memo) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.profileUrl = profileUrl;
        this.gender = gender;
        this.memo = memo;
    }

    public static Member of(String email, String nickname, String birthday, String profileUrl, Integer gender, String memo) {
        return new Member(email, nickname, birthday, profileUrl, gender, memo);
    }

    public static Member of(String email, String nickname, String birthday, String profileUrl, Integer gender) {
        return new Member(email, nickname, birthday, profileUrl, gender, null);
    }

    public static Member of(String email, String nickname, String profileUrl) {
        return new Member(email, nickname, null, profileUrl, null, null);
    }

}
