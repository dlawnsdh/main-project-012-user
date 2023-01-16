package com.example.mainproject012.service;

import com.example.mainproject012.dto.security.GoogleOAuth2Response;
import com.example.mainproject012.dto.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

/*@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthMemberService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberService memberService;

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();

        Mono<OAuth2User> oAuth2User = delegate.loadUser(userRequest);

        log.info("This is OAuthMemberService!!!!");

        GoogleOAuth2Response googleResponse = GoogleOAuth2Response.from(Objects.requireNonNull(oAuth2User.block()).getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = String.valueOf(googleResponse.email());

        return memberService.findMember(email)
                .map(MemberPrincipal::from)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(memberPrincipal -> {
                    if (memberPrincipal == null) {
                        return MemberPrincipal.from(
                                Objects.requireNonNull(memberService.saveMember(
                                        email,
                                        googleResponse.nickname(),
                                        null,
                                        googleResponse.photoUrl(),
                                        null
                                ).block())
                        );
                    }
                    return null;
                });
    }

}*/
