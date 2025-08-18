package io.github.sunday.devfolio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class CustomUser implements UserDetails {

    private Long userIdx;
    private String loginId;
    private String oauthProvider;
    private Collection<? extends GrantedAuthority> authorities;

    // 필드 기반 생성자
    public CustomUser(Long userIdx, String loginId, String oauthProvider, Collection<? extends GrantedAuthority> authorities) {
        this.userIdx = userIdx;
        this.loginId = loginId;
        this.oauthProvider = oauthProvider;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반이므로 비밀번호는 필요 없음
    }

    @Override
    public String getUsername() {
        return loginId; // Spring Security 식별자로 loginId 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}