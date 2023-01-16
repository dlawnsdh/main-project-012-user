package com.example.mainproject012.controller;

import com.example.mainproject012.repository.MemberRepository;
import com.example.mainproject012.service.MemberService;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository;

    @DeleteMapping("/{member-id}")
    public Mono<ResponseEntity<String>> deleteMember(@PathVariable("member-id") Long id) {
        return memberRepository.deleteById(id)
                .then(Mono.just(new ResponseEntity<>("삭제 완료", HttpStatus.NO_CONTENT)));
    }

}
