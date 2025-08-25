package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.auth.CustomUserDetailsService;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security의 UserDetails 인터페이스를 구현한 사용자 정의 클래스.
 * <p>
 * Spring Security 컨텍스트에 저장될 사용자 정보(Principal)를 캡슐화합니다.
 * 우리 애플리케이션의 {@link User} 엔티티를 멤버로 포함하여,
 * 인증 객체 내에서 사용자 엔티티에 직접 접근할 수 있도록 합니다.
 * </p>
 *
 * @author YourName
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see CustomUserDetailsService
 */
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    /** 애플리케이션의 사용자 정보를 담고 있는 원본 User 엔티티 */
    private final User user;

    /**
     * User 엔티티를 받아 CustomUserDetails 객체를 생성합니다.
     * @param user 데이터베이스에서 조회한 사용자 엔티티
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자에게 부여된 권한 목록을 반환합니다.
     * @return 권한 정보를 담은 Collection
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_USER");
        return authorities;
    }

    /**
     * 데이터베이스에 저장된 암호화된 비밀번호를 반환합니다.
     * @return 암호화된 비밀번호 문자열
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Spring Security에서 사용자를 식별하는 데 사용되는 이름(ID)을 반환합니다.
     * 우리 시스템에서는 로그인 아이디(loginId)를 사용합니다.
     * @return 로그인 아이디
     */
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    /**
     * 계정의 만료 여부를 반환합니다.
     * @return {@code true}이면 계정이 만료되지 않음
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정의 잠금 여부를 반환합니다.
     * @return {@code true}이면 계정이 잠기지 않음
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호)의 만료 여부를 반환합니다.
     * @return {@code true}이면 자격 증명이 만료되지 않음
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정의 활성화 여부를 반환합니다.
     * @return {@code true}이면 계정이 활성화됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User 구현
    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("email", user.getEmail());
        attrs.put("name", user.getNickname());
        attrs.put("providerId", user.getProviderId());
        return attrs;
    }

    @Override
    public String getName() {
        return user.getNickname(); // OAuth2User.getName() 반환
    }
}