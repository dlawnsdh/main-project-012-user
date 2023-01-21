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
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/followings/{member-id}")
    public Mono<ResponseEntity> followMember(@PathVariable("member-id") Long id,
                                             ServerHttpRequest request) {
        String email = extractEmail(request);
        return memberService.postFollow(email, id).map(ResponseEntity::ok);
    }

    @DeleteMapping("/followings/{member-id}")
    public Mono<ResponseEntity> followCancel(@PathVariable("member-id") Long id,
                                             ServerHttpRequest request) {
        String email = extractEmail(request);
        return memberService.cancelFollow(email, id).map(ResponseEntity::ok);
    }

    @GetMapping("/followings")
    public Flux<FollowResponseWithInfo> checkFollowings(ServerHttpRequest request) {
        String email = extractEmail(request);
        return followService.findFollowingByFollowerEmail(email);
    }

    @GetMapping("/followers")
    public Flux<FollowResponseWithInfo> checkFollowers(ServerHttpRequest request) {
        String email = extractEmail(request);
        return followService.findFollowerByFollowingEmail(email);
    }

    private String extractEmail(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assert bearerToken != null;
        bearerToken = bearerToken.substring(7);
        return jwtTokenProvider.getUserEmail(bearerToken);
    }

}
