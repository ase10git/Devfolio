package io.github.sunday.devfolio.service;


import io.github.sunday.devfolio.dto.ProfileUpdateRequest;
import io.github.sunday.devfolio.entity.table.user.EmailVerification;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.EmailVerificationRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ProfileUpdateService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;

    public void verifyPassword(User currentUser, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, currentUser.getPassword())) {
            throw new BadCredentialsException("비밀번호를 잘못 입력하셨습니다.");
        }
    }

    @Transactional
    public User updateProfile(User currentUser, ProfileUpdateRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String nickname = req.getNickname().trim();
        String password = req.getPassword();

        // 이메일 변경 시 이메일 인증 확인
        if (!currentUser.getEmail().equals(email)) {
            EmailVerification verification = emailVerificationRepository
                    .findTopByEmailOrderByExpiredAtDesc(email)
                    .orElse(null);

            // 이메일 중복 검사 (자기 자신 제외)
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            if (verification == null) {
                throw new IllegalArgumentException("이메일 인증을 진행해 주세요.");
            }

            if (!Boolean.TRUE.equals(verification.getVerified())) {
                throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
            }

            if (verification.getExpiredAt().isBefore(ZonedDateTime.now())) {
                throw new IllegalArgumentException("이메일 인증이 만료되었습니다. 다시 인증해 주세요.");
            }
        }
        // 닉네임 중복 검사 (자기 자신 제외)
        if (!currentUser.getNickname().equals(nickname)
                && userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        // 닉네임 유효성 검사
        if (!userService.isValidNickname(nickname)) {
            throw new IllegalArgumentException("닉네임은 한글/영문/숫자를 사용한 4~12자만 가능합니다.");
        }
        // 비밀번호 유효성 검사 (비밀번호 입력 시만)
        if (StringUtils.hasText(password) && !userService.isValidPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 영문+숫자 필수, 특수문자는 !@#$%^&*()만 가능하며 8~20자여야 합니다.");
        }

        // 변경사항 반영
        currentUser.setEmail(email);
        currentUser.setNickname(nickname);
        if (StringUtils.hasText(password)) {
            currentUser.setPassword(passwordEncoder.encode(password));
        }
        currentUser.setGithubUrl(req.getGithubUrl());
        currentUser.setBlogUrl(req.getBlogUrl());
        currentUser.setAffiliation(req.getAffiliation());
        currentUser.setProfileImg(req.getProfileImg());

        return userRepository.save(currentUser);
    }

    public boolean isNicknameDuplicateForEdit(String nickname, Long userIdx) {
        return userRepository.existsByNicknameAndUserIdxNot(nickname, userIdx);
    }
}

