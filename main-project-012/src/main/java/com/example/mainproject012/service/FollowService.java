package com.example.mainproject012.service;

import com.example.mainproject012.domain.Follow;
import com.example.mainproject012.dto.MemberDto;
import com.example.mainproject012.dto.responose.FollowResponseWithInfo;
import com.example.mainproject012.repository.FollowRepository;
import com.example.mainproject012.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public Mono<Void> post(Long followerId, Long followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .switchIfEmpty(followRepository.save(Follow.of(followerId, followingId)))
                .then();
    }

    public Mono<Void> cancel(Long followerId, Long followingId) {
        return followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Mono<Long> countFollowing(Long followerId) {
        return followRepository.countAllByFollowerId(followerId);
    }

    public Mono<Long> countFollower(Long followingId) {
        return followRepository.countAllByFollowerId(followingId);
    }

    public Flux<FollowResponseWithInfo> findFollowingByFollowerId(Long followerId) {
        return followRepository.findAllByFollowerId(followerId)
                .flatMap(follow -> memberRepository.findById(follow.getFollowingId()))
                .map(MemberDto::from)
                .map(FollowResponseWithInfo::from);
    }

    public Flux<FollowResponseWithInfo> findFollowerByFollowingId(Long followingId) {
        return followRepository.findAllByFollowingId(followingId)
                .flatMap(follow -> memberRepository.findById(follow.getFollowerId()))
                .map(MemberDto::from)
                .map(FollowResponseWithInfo::from);
    }

}
