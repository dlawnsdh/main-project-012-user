package com.example.mainproject012.service;

import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.MemberDto;
import com.example.mainproject012.dto.request.UserPatchRequest;
import com.example.mainproject012.dto.responose.*;
import com.example.mainproject012.repository.MemberCustomRepository;
import com.example.mainproject012.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final FollowService followService;

    //
    private final MemberCustomRepository memberCustomRepository;

    @Transactional(readOnly = true)
    public Flux<MemberResponse> findAllWithInfo() {
        return memberCustomRepository.findWithFollow();
    }

    /*@Transactional(readOnly = true)
    public Mono<MemberResponseWithInfo> findMemberByEmailV2(String email) {


        return memberRepository.findByEmail(email)
                .map(MemberDto::from)
                .map(MemberResponse::from)
                .map(mp -> {
                    Long followingCount = followService.countFollowing(mp.id()).block();
                    Long followerCount = followService.countFollower(mp.id()).block();
                    List<SimplePostResponse> postResponse = List.of();
                    List<PickerResponse> pickerResponse = List.of();
                    return MemberResponseWithInfo.of(mp, followingCount, followerCount, postResponse, pickerResponse);
                });
    }*/

    //

    @Transactional(readOnly = true)
    public Mono<MemberDto> findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    public Mono<MemberDto> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberDto::from);
    }

    /*@Transactional(readOnly = true)
    public Mono<MemberResponseWithInfo> findMemberByEmail(String email) {
        //TODO: 리팩토링
        //TODO: post 와 picker 로 부터 정보 얻어와서 매핑 필요
        List<SimplePostResponse> postResponse = List.of();
        List<PickerResponse> pickerResponse = List.of();

        //TODO: 리팩토링
        Long followingCount = countFollowing(email).block();
        Long followerCount = countFollower(email).block();

        return memberRepository.findByEmail(email)
                .map(MemberDto::from)
                .map(MemberResponse::from)
                .map(memberResponse -> {
                    return MemberResponseWithInfo.of(memberResponse, followingCount, followerCount, postResponse, pickerResponse);
                });
    }*/

    @Transactional(readOnly = true)
    public Mono<MemberDto> findMember(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    public Flux<MemberResponse> findMembers(String nickname) {
        return memberRepository.findAllByNickname(nickname)
                .map(MemberDto::from)
                .map(MemberResponse::from);


    }

    @Transactional
    public Mono<MemberDto> saveMember(String email, String nickname, String birthday, String profileUrl, Integer gender, String registrationId) {
        log.info("This is saveMember in MemberService!!!!");

        Member member = Member.of(email, nickname, birthday, profileUrl, gender, registrationId);
        log.info("{}", member);
        Mono<Member> savedMember = memberRepository.save(member);

        return savedMember.map(MemberDto::from);
    }

    @Transactional
    public Mono<MemberDto> saveMember(MemberDto dto) {
        log.info("This is saveMember in MemberService!!!!");

        Member member = dto.toEntity();
        log.info("{}", member);
        return memberRepository.save(member)
                .map(MemberDto::from);
    }

    @Transactional
    public Mono<MemberResponse> updateMember(Long id, UserPatchRequest request) {
        log.info("This is updateMember in MemberService!!!!");

        return memberRepository.findById(id)
                .flatMap(member -> {
                    if (request.nickname() != null) member.setNickname(request.nickname());
                    if (request.profileUrl() != null) member.setProfileUrl(request.profileUrl());
                    if (request.memo() != null) member.setMemo(request.memo());

                    return memberRepository.save(member);
                })
                .map(MemberDto::from)
                .map(MemberResponse::from);
    }

    /*@Transactional
    public Mono<Void> postFollow(Long followerId, Long followingId) {
        return getMemberEmail(followingId)
                .flatMap(followingEmail -> {
                    return followService.post(email, followingEmail);
                });
    }

    @Transactional
    public Mono<Void> cancelFollow(String email, Long followingId) {
        return getMemberEmail(followingId)
                .flatMap(followingEmail -> {
                    return followService.cancel(email, followingEmail);
                });
    }*/

    /*public Mono<Long> countFollowing(String email) {
        return followService.countFollowing(email);
    }

    public Mono<Long> countFollower(String email) {
        return followService.countFollower(email);
    }

    public Flux<FollowResponseWithInfo> checkFollowing(String email) {
        return followService.findFollowingByFollowerId(email);
    }

    public Flux<FollowResponseWithInfo> checkFollower(String email) {
        return followService.findFollowerByFollowingId(email);
    }*/

    @Transactional(readOnly = true)
    public Mono<Void> verifyExistEmail(String email) {
        return memberRepository.findByEmail(email)
                .flatMap(findMember -> {
                    if (findMember != null) {
                        return Mono.error(new IllegalArgumentException("해당 유저가 존재함"));
                    }
                    return Mono.empty();
                });
    }

    @Transactional(readOnly = true)
    public Mono<Void> verifyExistId(Long memberId) {
        return memberRepository.findById(memberId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("해당 유저가 존재하지 않음")))
                .then();
    }

    private Mono<Long> getMemberId(String email) {
        return memberRepository.findByEmail(email).map(Member::getMemberId);
    }

    private Mono<String> getMemberEmail(Long id) {
        return memberRepository.findById(id).map(Member::getEmail);
    }

}
