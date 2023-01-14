package com.example.mainproject012.handler;

import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.dto.security.MemberPrincipal;
import com.example.mainproject012.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
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

import java.net.URI;
import java.util.Collection;
import java.util.Date;

@Slf4j
@NoArgsConstructor
public class OAuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    private URI location = URI.create("/");
    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
    private ServerRequestCache requestCache = new WebSessionServerRequestCache();
    private JwtTokenProvider jwtTokenProvider;
    //private OAuthMemberService oAuthMemberService;
    private MemberService memberService;

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.jwtTokenProvider = jwtTokenProvider;
        //this.oAuthMemberService = oAuthMemberService;
        this.memberService = memberService;
    }

    public OAuthSuccessHandler(String location, JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.location = URI.create(location);
        this.jwtTokenProvider = jwtTokenProvider;
        //this.oAuthMemberService = oAuthMemberService;
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


        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");
        String picture = (String) oAuth2User.getAttributes().get("picture");
        MemberPrincipal principal;
        if (!(oAuth2User instanceof DefaultOidcUser)) {
            principal = (MemberPrincipal) authentication.getPrincipal();
            email = principal.email();

            if (memberService.verifyExistEmail(email) != null) {
                memberService.saveMember(
                        email,
                        principal.nickname(),
                        null,
                        principal.profileUrl(),
                        null
                ).subscribe();
            }
        }

        if (memberService.verifyExistEmail(email) != null) {
            memberService.saveMember(
                    email,
                    name,
                    null,
                    picture,
                    null
            ).subscribe();
        }

        String accessToken = delegateAccessToken(authentication);
        String refreshToken = delegateRefreshToken(authentication);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        location = createUri(accessToken, refreshToken);

        ServerWebExchange exchange = webFilterExchange.getExchange();
        return this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
                .flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, location));
    }

    public void setLocation(URI location) {
        Assert.notNull(location, "location cannot be null");
        this.location = location;
    }

    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }

    private String delegateAccessToken(Authentication authentication) {
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());
        return jwtTokenProvider.createAccessToken(authentication, expiration);
    }

    private String delegateRefreshToken(Authentication authentication) {
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());
        return jwtTokenProvider.createRefreshToken(authentication, expiration);
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
