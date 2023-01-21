package com.example.mainproject012.repository;

import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.responose.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Comparator;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberCustomRepository {
    private static final String MEMBER_ID_FIELD_NAME = "memberId";
    private final DatabaseClient databaseClient;

    public Flux<MemberResponse> findWithFollow() {
        var sqlWithFollow = """
                SELECT 
                    m.member_id as memberId, m.email as email, m.nickname as nickname,
                    m.birthday as birthday, m.profile_url as profileUrl, 
                    m.gender as gender, m.memo as memo, m.registration_id as registrationId,
                    m.created_at as createdAt, m.modified_at as modifiedAt,
                    (SELECT COUNT(f.follower_email) as followers FROM follow f WHERE m.email = f.following_email),
                    (SELECT COUNT(f.following_email) as followings FROM follow f WHERE m.email = f.follower_email)   
                FROM member m
                """;

        return databaseClient.sql(sqlWithFollow)
                .fetch().all()
                .sort(Comparator.comparing(result -> (Long) result.get(MEMBER_ID_FIELD_NAME)))
                .bufferUntilChanged(result -> result.get(MEMBER_ID_FIELD_NAME))
                .map(result -> {
                    var followers = Long.parseLong(result.get(0).get("(SELECT COUNT(f.follower_email) as followers FROM follow f WHERE m.email = f.following_email)").toString());
                    var followings = Long.parseLong(result.get(0).get("(SELECT COUNT(f.following_email) as followings FROM follow f WHERE m.email = f.follower_email)").toString());

                    var row = result.get(0);
                    return MemberResponse.from(Member.builder()
                            .memberId((Long) row.get(MEMBER_ID_FIELD_NAME))
                            .email((String) row.get("email"))
                            .nickname((String) row.get("nickname"))
                            .birthday((String) row.get("birthday"))
                            .profileUrl((String) row.get("profileUrl"))
                            .gender((Integer) row.get("gender"))
                            .memo((String) row.get("memo"))
                            .registrationId((String) row.get("registrationId"))
                            .createdAt((LocalDateTime) row.get("createdAt"))
                            .modifiedAt((LocalDateTime) row.get("modifiedAt"))
                            .followings(followings)
                            .followers(followers)
                            .build());
                });
    }

    /*public Flux<Member> findWithFollowV2() {
        var sqlWithFollow = """
                SELECT 
                    m.member_id as memberId, m.email as email, m.nickname as nickname,
                    m.birthday as birthday, m.profile_url as profileUrl, 
                    m.gender as gender, m.memo as memo, m.registration_id as registrationId,
                    m.created_at as createdAt, m.modified_at as modifiedAt,
                    f.id as followId, f.following_id as followingId, f.follower_id as followerId,
                    f.created_at as createdAt, f.modified_at as modifiedAt
                FROM member m
                INNER JOIN follow f
                ON m.member_id = f.follower_id
                """;

        return databaseClient.sql(sqlWithFollow)
                .fetch().all()
                .sort(Comparator.comparing(result -> (Long) result.get(MEMBER_ID_FIELD_NAME)))
                .bufferUntilChanged(result -> result.get(MEMBER_ID_FIELD_NAME))
                .map(result -> {
                    var follows = result.stream()
                            .map(row -> Follow.builder()
                                    .id((Long) row.get("followId"))
                                    .followingId((Long) row.get("followingId"))
                                    .followerId((Long) row.get("followerId"))
                                    .createdAt((LocalDateTime) row.get("createdAt"))
                                    .modifiedAt((LocalDateTime) row.get("modifiedAt"))
                                    .build())
                            .toList();

                    var row = result.get(0);
                    return Member.builder()
                            .memberId((Long) row.get(MEMBER_ID_FIELD_NAME))
                            .email((String) row.get("email"))
                            .nickname((String) row.get("nickname"))
                            .birthday((String) row.get("birthday"))
                            .profileUrl((String) row.get("profileUrl"))
                            .gender((Integer) row.get("gender"))
                            .memo((String) row.get("memo"))
                            .registrationId((String) row.get("registrationId"))
                            .createdAt((LocalDateTime) row.get("createdAt"))
                            .modifiedAt((LocalDateTime) row.get("modifiedAt"))
                            .follows(follows)
                            .build();
                });
    }*/

}
