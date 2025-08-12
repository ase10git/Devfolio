package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.dto.ProfileDto;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.FollowRepository;
import io.github.sunday.devfolio.repository.ResumeRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepo;
    private final FollowRepository followrepo;
    private final ResumeRepository resumerepo;


    /**
     * 프로필 정보 조회
     * @param targetUserId 프로필 대상 사용자 ID
     * @param currentUser 현재 로그인사용자
     * @return 프로필 표시용 DTO
     * @throws IllegalArgumentException 대상 사용자가 존재하지 않읈시 발생
     */
    public ProfileDto getProfile(Long targetUserId, User currentUser) {
        User target = userRepo.findById(targetUserId)
                .orElseThrow() -> new IllegalArgumentException("User not found")
    }
}
