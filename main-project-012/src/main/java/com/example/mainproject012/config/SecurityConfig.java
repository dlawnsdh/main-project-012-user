package com.example.mainproject012.config;

import com.example.mainproject012.auth.JwtAuthenticationFilter;
import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.domain.Member;
import com.example.mainproject012.dto.MemberDto;
import com.example.mainproject012.dto.security.GoogleOAuth2Response;
import com.example.mainproject012.dto.security.KakaoOAuth2Response;
import com.example.mainproject012.dto.security.MemberPrincipal;
import com.example.mainproject012.handler.OAuthSuccessHandler;
import com.example.mainproject012.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.DELETE, "/members/**").permitAll()
                        .pathMatchers("/login").permitAll()
                        .anyExchange().authenticated()
                )
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // stateless
                .oauth2Login(oauth -> oauth.authenticationSuccessHandler(new OAuthSuccessHandler(jwtTokenProvider)))
                .exceptionHandling()
                .accessDeniedHandler((exchange, exception) -> Mono.error(new RuntimeException("접근 권한 없음")))
                        .and()
                .addFilterAt(new JwtAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcOAuth2UserService(MemberService memberService) {
        final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return userRequest -> {
            Mono<OidcUser> oidcUser = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            return oidcUser
                    .map(OAuth2AuthenticatedPrincipal::getAttributes)
                    .map(GoogleOAuth2Response::from)
                    .map(GoogleOAuth2Response::toPrincipal)
                    .flatMap(principal -> {
                        Mono<MemberPrincipal> mp = memberService.verifyExistEmail(principal.email())
                                .then(memberService.saveMember(principal.toDto(registrationId)))
                                .map(MemberPrincipal::from);
                        return mp;
                    });
        };
    }

    @Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(MemberService memberService) {
        final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();

        return userRequest -> {
            Mono<OAuth2User> oAuth2User = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            return oAuth2User
                    .map(OAuth2AuthenticatedPrincipal::getAttributes)
                    .map(KakaoOAuth2Response::from)
                    .map(KakaoOAuth2Response::toPrincipal)
                    .flatMap(principal -> {
                        Mono<MemberPrincipal> mp = memberService.verifyExistEmail(principal.email())
                                .then(memberService.saveMember(principal.toDto(registrationId)))
                                .map(MemberPrincipal::from);
                        return mp;
                    });
        };
    }

}
