package io.github.sunday.devfolio.service.auth;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.entity.table.user.AuthProvider;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 구글에서 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        // DB에서 사용자 조회
        User user = userService.findByEmail(email);

        if (user != null) {
            // 기존 로컬 회원이 존재하면 소셜 로그인 차단
            if (user.getOauthProvider() == AuthProvider.LOCAL) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("email_exists", "이미 사용 중인 이메일입니다. 기존 계정으로 로그인해 주세요.", null)
                );
            }
            // 기존 소셜 회원이면 그대로 진행
        } else {
            // 신규 소셜 회원이면 등록
            String nickname = userService.generateValidNickname(name);
            String generatedId = userService.generateUserId(email);

            user = new User(
                    generatedId,
                    email,
                    nickname,
                    null,
                    providerId,
                    AuthProvider.GOOGLE
            );

            userService.saveUser(user);
        }

        return new CustomUserDetails(user);
    }
}