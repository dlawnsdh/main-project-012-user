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

    public Mono<Void> post(String followerEmail, String followingEmail) {
        return followRepository.findByFollowerEmailAndFollowingEmail(followerEmail, followingEmail)
                .switchIfEmpty(followRepository.save(Follow.of(followerEmail, followingEmail)))
                .then();
    }

    public Mono<Void> cancel(String followerEmail, String followingEmail) {
        return followRepository.deleteByFollowerEmailAndFollowingEmail(followerEmail, followingEmail);
    }

    public Mono<Long> countFollowing(String followerEmail) {
        return followRepository.countAllByFollowerEmail(followerEmail);
    }

    public Mono<Long> countFollower(String followingEmail) {
        return followRepository.countAllByFollowerEmail(followingEmail);
    }

    public Flux<FollowResponseWithInfo> findFollowingByFollowerEmail(String followerEmail) {
        return followRepository.findAllByFollowerEmail(followerEmail)
                .flatMap(follow -> memberRepository.findByEmail(follow.getFollowingEmail()))
                .map(MemberDto::from)
                .map(FollowResponseWithInfo::from);
    }

    public Flux<FollowResponseWithInfo> findFollowerByFollowingEmail(String followingEmail) {
        return followRepository.findAllByFollowingEmail(followingEmail)
                .flatMap(follow -> memberRepository.findByEmail(follow.getFollowerEmail()))
                .map(MemberDto::from)
                .map(FollowResponseWithInfo::from);
    }

}
