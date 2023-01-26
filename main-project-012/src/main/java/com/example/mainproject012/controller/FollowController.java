package com.example.mainproject012.controller;

import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.dto.responose.FollowResponseWithInfo;
import com.example.mainproject012.service.FollowService;
import com.example.mainproject012.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/followings/{member-id}")
    public Mono<ResponseEntity> followMember(@PathVariable("member-id") Long followingId,
                                             ServerHttpRequest request) {
        Long followerId = extractId(request);
        return followService.post(followerId, followingId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/followings/{member-id}")
    public Mono<ResponseEntity> followCancel(@PathVariable("member-id") Long followingId,
                                             ServerHttpRequest request) {
        Long followerId = extractId(request);
        return followService.cancel(followerId, followingId).map(ResponseEntity::ok);
    }

    @GetMapping("/followings")
    public Flux<FollowResponseWithInfo> checkFollowings(ServerHttpRequest request) {
        Long id = extractId(request);
        return followService.findFollowingByFollowerId(id);
    }

    @GetMapping("/followers")
    public Flux<FollowResponseWithInfo> checkFollowers(ServerHttpRequest request) {
        Long id = extractId(request);
        return followService.findFollowerByFollowingId(id);
    }

    private Long extractId(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assert bearerToken != null;
        bearerToken = bearerToken.substring(7);
        return jwtTokenProvider.getUserId(bearerToken);
    }

}
