package io.github.sunday.devfolio.service;


import io.github.sunday.devfolio.dto.ProfileUpdateRequest;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProfileUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void verifyPassword(User currentUser, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, currentUser.getPassword())) {
            throw new BadCredentialsException("비밀번호를 잘못 입력하셨습니다.");
        }
    }

    @Transactional
    public User updateProfile(User currentUser, ProfileUpdateRequest req) {
        // 중복 검사 (자기 자신 제외)
        if (!currentUser.getEmail().equals(req.getEmail())
                && userRepository.existsByEmail(req.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (!currentUser.getNickname().equals(req.getNickname())
                && userRepository.existsByNickname(req.getNickname().trim())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 변경사항 반영
        currentUser.setEmail(req.getEmail().trim().toLowerCase());
        currentUser.setNickname(req.getNickname().trim());
        if (StringUtils.hasText(req.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(req.getPassword()));
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

