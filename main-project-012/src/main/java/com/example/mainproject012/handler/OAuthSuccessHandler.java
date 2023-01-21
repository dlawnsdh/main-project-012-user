package com.example.mainproject012.handler;

import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.dto.security.MemberPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;


@Slf4j
public class OAuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    private URI location = URI.create("/");
    private final ServerRequestCache requestCache = new WebSessionServerRequestCache();
    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
    private JwtTokenProvider jwtTokenProvider;

    public OAuthSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();

        String accessToken = delegateAccessToken(principal);
        String refreshToken = delegateRefreshToken(principal);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        location = createUri(accessToken, refreshToken);

        ServerWebExchange exchange = webFilterExchange.getExchange();
        return this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
                .flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, location));
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
