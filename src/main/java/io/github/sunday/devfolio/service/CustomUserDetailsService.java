package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.config.CustomUser;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // DB에서 로그인 ID로 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // CustomUser 생성 (JWT 기반이므로 password는 null, 권한은 비워둠)
        return new CustomUser(
                user.getUserIdx(),              // idx
                user.getLoginId(),              // loginId
                user.getOauthProvider().name(), // oauthProvider
                Collections.emptyList()         // authorities
        );
    }
}