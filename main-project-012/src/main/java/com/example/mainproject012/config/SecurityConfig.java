package com.example.mainproject012.config;

import com.example.mainproject012.auth.JwtAuthenticationFilter;
import com.example.mainproject012.auth.JwtTokenProvider;
import com.example.mainproject012.dto.security.GoogleOAuth2Response;
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
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
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
//@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    //private final OAuthMemberService oAuthMemberService;
    private final MemberService memberService;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http
                                            //OAuthMemberService oAuthMemberService
                                            //MemberService memberService
                                            //ReactiveAuthenticationManager reactiveAuthenticationManager
    ) throws Exception {
        return http
                .authorizeExchange(auth -> auth
                        //.pathMatchers(HttpMethod.GET, "/").hasRole("USER")
                        .pathMatchers(HttpMethod.DELETE, "/members/**").permitAll()
                        .pathMatchers("/login").permitAll()
                        .anyExchange().authenticated()
                )
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                //.authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2Login(oauth -> oauth.authenticationSuccessHandler(new OAuthSuccessHandler(jwtTokenProvider, memberService)))
                .exceptionHandling()
                .accessDeniedHandler((exchange, exception) -> Mono.error(new RuntimeException("접근 권한 없음")))
                        .and()
                .addFilterAt(new JwtAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    /*@Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {
        WebClientReactiveAuthorizationCodeTokenResponseClient client = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        return new OAuth2LoginReactiveAuthenticationManager(client, oAuth2UserService);
    }*/

    //TODO: 반드시 얘를 거쳐서 토큰이 생성되어야 함!
    /*@Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(MemberService memberService) {
        log.info("This is oAuth2UserService in SecurityConfig!!!!");
        final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();

        return userRequest -> {
            Mono<OAuth2User> oAuth2User = delegate.loadUser(userRequest);

            //log.info("This is oAuth2UserService in SecurityConfig!!!!");

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
        };
    }*/

    /*@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) {
        return http
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        }))
                        .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        }))
                )
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                //.authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        //.pathMatchers("/login").permitAll()
                        //.pathMatchers("/about").permitAll()
                        //.pathMatchers("/h2-console").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Client()
                        .and()
                .oauth2Login()
                .authenticationSuccessHandler(new OAuthSuccessHandler())
                //.authenticationFailureHandler()
                        .and()
                .build();
    }*/

    /*private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        return new DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository);
    }*/

}