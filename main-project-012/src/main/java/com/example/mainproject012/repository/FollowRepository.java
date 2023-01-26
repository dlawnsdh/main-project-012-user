package com.example.mainproject012.repository;

import com.example.mainproject012.domain.Follow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FollowRepository extends ReactiveCrudRepository<Follow, Long> {
    Mono<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Mono<Void> deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Flux<Follow> findAllByFollowerId(Long followerId);

    Flux<Follow> findAllByFollowingId(Long followingId);

    @Query("select count(following_id) from follow where follower_id = :followerId")
    Mono<Long> countAllByFollowingId(Long followerId);

    @Query("select count(follower_id) from follow where following_id = :followingId")
    Mono<Long> countAllByFollowerId(Long followingId);

    @Query("select count(following_id), count(follower_id) from follow where following_id =: memberId or follower_id =: memberId")
    Flux<Long> countAllByMemberEmail(Long memberId);

}
