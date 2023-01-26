package com.example.mainproject012.dto.security;

import com.example.mainproject012.dto.MemberDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record MemberPrincipal(
        String id,
        String email,
        String nickname,
        String birthday,
        Integer gender,
        String profileUrl,
        String memo,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> oAuth2Attributes
) implements OAuth2User, OidcUser {
    public static MemberPrincipal of(String id, Collection<? extends GrantedAuthority> authorities) {
        return new MemberPrincipal(id, null, null, null, null, null, null, authorities, Map.of());
    }

    public static MemberPrincipal of(String id, String email, String nickname, String birthday, Integer gender, String profileUrl, String memo) {
        return of(id, email, nickname, birthday, gender, profileUrl, memo, Map.of());
    }

    public static MemberPrincipal of(String id, String email, String nickname, String birthday, Integer gender, String profileUrl, String memo, Map<String, Object> oAuth2Attributes) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new MemberPrincipal(
                id,
                email,
                nickname,
                birthday,
                gender,
                profileUrl,
                memo,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()),
                oAuth2Attributes
        );
    }

    public static MemberPrincipal from(MemberDto dto) {
        return MemberPrincipal.of(
                String.valueOf(dto.memberId()),
                dto.email(),
                dto.nickname(),
                dto.birthday(),
                dto.gender(),
                dto.profileUrl(),
                dto.memo()
        );
    }

    public MemberDto toDto(String registrationId) {
        return MemberDto.of(
                email,
                nickname,
                birthday,
                profileUrl,
                gender,
                memo,
                registrationId
        );
    }

    @Override public Map<String, Object> getAttributes() { return oAuth2Attributes; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getName() { return nickname; }

    @Override public Map<String, Object> getClaims() { return oAuth2Attributes; }
    @Override public OidcUserInfo getUserInfo() { return null; }
    @Override public OidcIdToken getIdToken() { return null; }

    public enum RoleType {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

}
