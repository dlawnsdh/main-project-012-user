package com.example.mainproject012.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Builder
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table
public class Member {
    @Id private Long memberId;

    private String email;

    @Setter private String nickname;

    private String birthday;

    @Setter private String profileUrl;

    private Integer gender;

    @Setter private String memo;

    private String registrationId;

    @CreatedDate private LocalDateTime createdAt;

    @LastModifiedDate private LocalDateTime modifiedAt;

    @Transient private Long followings;
    @Transient private Long followers;
    //@Transient private List<Follow> follows;
    private Member(String email, String nickname, String birthday, String profileUrl, Integer gender, String memo, String registrationId) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.profileUrl = profileUrl;
        this.gender = gender;
        this.memo = memo;
        this.registrationId = registrationId;
    }

    public static Member of(String email, String nickname, String birthday, String profileUrl, Integer gender, String memo, String registrationId) {
        return new Member(email, nickname, birthday, profileUrl, gender, memo, registrationId);
    }

    public static Member of(String email, String nickname, String birthday, String profileUrl, Integer gender, String registrationId) {
        return new Member(email, nickname, birthday, profileUrl, gender, null, registrationId);
    }

    public static Member of(String email, String nickname, String profileUrl, String registrationId) {
        return new Member(email, nickname, null, profileUrl, null, null, registrationId);
    }

}
