package com.example.mainproject012.service;

import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.MemberDto;
import com.example.mainproject012.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Mono<MemberDto> findMember(String email) {
        log.info("This is findMember in MemberService!!!!");

        return memberRepository.findByEmail(email).map(MemberDto::from);
    }

    @Transactional
    public Mono<MemberDto> saveMember(String email, String nickname, String birthday, String profileUrl, Integer gender) {
        log.info("This is saveMember in MemberService!!!!");

        Member member = Member.of(email, nickname, birthday, profileUrl, gender);
        log.info("{}", member);
        Mono<Member> savedMember = memberRepository.save(member);

        return savedMember.map(MemberDto::from);
    }

    @Transactional
    public Mono<Void> saveMember(MemberDto dto) {
        log.info("This is saveMember in MemberService!!!!");

        Member member = dto.toEntity();
        log.info("{}", member);
        return memberRepository.save(member).then(Mono.empty());
    }

    @Transactional(readOnly = true)
    public Mono<Void> verifyExistEmail(String email) {
        log.info("This is verifyExistEmail in MemberService!!!!");

        return memberRepository.findByEmail(email)
                .flatMap(findMember -> {
                    if (findMember != null) {
                        return Mono.error(new IllegalArgumentException("해당 유저가 존재함"));
                    }
                    return Mono.empty();
                });
    }

}
