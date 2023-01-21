package com.example.mainproject012.controller;

import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.MemberDto;
import com.example.mainproject012.dto.request.UserPatchRequest;
import com.example.mainproject012.dto.responose.*;
import com.example.mainproject012.repository.MemberRepository;
import com.example.mainproject012.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository; // 기능 확인 위해 임시로 추가
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/mypage")
    public Mono<ResponseEntity<MemberResponse>> myPage(ServerHttpRequest request) {
        String email = extractEmail(request);
        return memberService.findAllWithInfo()
                .filter(member -> member.email().equals(email))
                .last()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{member-id}")
    public Mono<ResponseEntity<MemberResponse>> findMember(@PathVariable("member-id") Long id) {
        return memberService.findAllWithInfo()
                .filter(member -> member.id().equals(id))
                .last()
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<MemberSearchResponse> findMembers(@RequestBody Map<String, String> nicknameMap) {
        return memberService.findAllWithInfo()
                .filter(member -> member.nickname().equals(nicknameMap.get("nickname")))
                .map(MemberSearchResponse::from);
    }

    /*@GetMapping("/mypage")
    public Mono<ResponseEntity<MemberResponseWithInfo>> myPage(ServerHttpRequest request) {
        String email = extractEmail(request);
        return memberService.findMemberByEmail(email).map(ResponseEntity::ok);
    }*/

    /*@GetMapping("/{member-id}")
    public Mono<ResponseEntity<MemberResponseWithInfo>> findMember(@PathVariable("member-id") Long id) {
        return memberService.findMemberById(id).map(ResponseEntity::ok);
    }*/

    /*@GetMapping
    public Flux<MemberResponse> findMembers(@RequestBody Map<String, String> nicknameMap) {
        return memberService.findMembers(nicknameMap.get("nickname"));
    }*/

    @PatchMapping
    public Mono<ResponseEntity<MemberPatchResponse>> updateMember(@RequestBody UserPatchRequest patchRequest,
                                                                           ServerHttpRequest request) {
        String email = extractEmail(request);
        return memberService.updateMember(email, patchRequest)
                .map(MemberPatchResponse::from)
                .map(ResponseEntity::ok);
    }

    // 기능 확인 위해 임시로 추가
    @DeleteMapping("/{member-id}")
    public Mono<ResponseEntity<String>> deleteMember(@PathVariable("member-id") Long id) {
        return memberRepository.deleteById(id)
                .then(Mono.just(new ResponseEntity<>("삭제 완료", HttpStatus.NO_CONTENT)));
    }

    private String extractEmail(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assert bearerToken != null;
        bearerToken = bearerToken.substring(7);
        return jwtTokenProvider.getUserEmail(bearerToken);
    }

}
