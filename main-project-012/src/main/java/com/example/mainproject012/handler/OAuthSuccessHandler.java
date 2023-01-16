package com.example.mainproject012.handler;

import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.dto.security.GoogleOAuth2Response;
import com.example.mainproject012.dto.security.KakaoOAuth2Response;
import com.example.mainproject012.dto.security.MemberPrincipal;
import com.example.mainproject012.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class OAuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    private URI location = URI.create("/");
    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
    private ServerRequestCache requestCache = new WebSessionServerRequestCache();
    private JwtTokenProvider jwtTokenProvider;
    private MemberService memberService;

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    public OAuthSuccessHandler(String location, JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.location = URI.create(location);
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    public void setRequestCache(ServerRequestCache requestCache) {
        Assert.notNull(requestCache, "requestCache cannot be null");
        this.requestCache = requestCache;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        var oAuth2User = (OAuth2User) authentication.getPrincipal();

        log.info("---------------------------------------");
        log.info("This is OAuthSuccessHandler!!!");
        for (String key : oAuth2User.getAttributes().keySet())
            log.info("User info : {} : {}",key,  oAuth2User.getAttributes().get(key));
        log.info("---------------------------------------");

        MemberPrincipal principal;
        if (oAuth2User instanceof DefaultOidcUser) {
            GoogleOAuth2Response google = GoogleOAuth2Response.from(oAuth2User.getAttributes());
            principal = google.toPrincipal();

            memberService.verifyExistEmail(principal.email())
                    .then(memberService.saveMember(principal.toDto()))
                    .subscribe();
        }
        else {
            KakaoOAuth2Response kakao = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            principal = kakao.toPrincipal();

            memberService.verifyExistEmail(principal.email())
                    .then(memberService.saveMember(principal.toDto()))
                    .subscribe();
        }

        String accessToken = delegateAccessToken(principal);
        String refreshToken = delegateRefreshToken(principal);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        location = createUri(accessToken, refreshToken);

        ServerWebExchange exchange = webFilterExchange.getExchange();
        return this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
                .flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, location));
    }

    /*@Override
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
    }*/

    public void setLocation(URI location) {
        Assert.notNull(location, "location cannot be null");
        this.location = location;
    }

    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }

    private String delegateAccessToken(MemberPrincipal principal) {
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());
        return jwtTokenProvider.createAccessToken(principal, expiration);
    }

    private String delegateRefreshToken(MemberPrincipal principal) {
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());
        return jwtTokenProvider.createRefreshToken(principal, expiration);
    }

    private URI createUri(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .path("/token")
                .queryParams(queryParams)
                .build()
                .toUri();
    }

}
