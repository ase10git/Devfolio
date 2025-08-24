package io.github.sunday.devfolio.controller.user;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.user.ProfileDto;
import io.github.sunday.devfolio.dto.user.ProfileUpdateRequest;
import io.github.sunday.devfolio.entity.table.profile.Resume;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.user.ProfileService;
import io.github.sunday.devfolio.service.user.ProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 프로필 화면/수정 기능을 제공하는 REST 컨트롤러.
 *
 * <p>엔드포인트 개요</p>
 * <ul>
 *   <li>GET /api/profile/{userIdx} : 프로필 헤더/요약 정보 조회</li>
 *   <li>POST /api/profile/{userIdx}/follow : 팔로우/언팔로우 토글</li>
 *   <li>GET /api/profile/{userIdx}/resumes : 이력서 목록 조회</li>
 *   <li>POST /api/profile/verify-password : 2차 인증(비밀번호 검증)</li>
 *   <li>PUT /api/profile/edit : 프로필 정보 수정</li>
 * </ul>
 *
 * <p>인증</p>
 * <ul>
 *   <li>{@code @AuthenticationPrincipal} 로 현재 사용자 정보를 주입받는다.</li>
 *   <li>프로덕션에서는 User 엔티티 대신 커스텀 UserDetails를 사용하는 구성이 일반적.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileUpdateService updateService;

    /**
     * 프로필 요약 정보를 조회한다.
     *
     * @param userIdx      대상 사용자 ID
     * @return 프로필 DTO
     */
    @GetMapping("/{userIdx}")
    public ProfileDto getProfile(
            @PathVariable Long userIdx,
            @AuthenticationPrincipal CustomUserDetails currentUserDetails
    ) {
        User currentUser = currentUserDetails != null ? currentUserDetails.getUser() : null;
        return profileService.getProfile(userIdx, currentUser);
    }

    /**
     * 팔로우/언팔로우 상태를 토글한다.
     *
     * @param userIdx      대상 사용자 ID
     * @return {"following": true|false}
     */
    @PostMapping("/{userIdx}/follow")
    public Map<String, Boolean> toggleFollow(
            @PathVariable Long userIdx,
            @AuthenticationPrincipal CustomUserDetails currentUserDetails
    ) {
        User currentUser = currentUserDetails != null ? currentUserDetails.getUser() : null;
        boolean nowFollowing = profileService.toggleFollow(currentUser, userIdx);
        return Collections.singletonMap("following", nowFollowing);
    }

    /**
     * 대상 사용자의 이력서 목록을 조회한다.
     *
     * @param userIdx 대상 사용자 ID
     * @return 이력서 리스트
     */
    @GetMapping("/{userIdx}/resumes")
    public List<Resume> getResumes(
            @PathVariable Long userIdx
    ) {
        return profileService.getResumes(userIdx);
    }

    /**
     * 프로필 수정 전 비밀번호 재확인(2차 인증).
     *
     * @param body        {"password": "입력값"}
     * @return 200 OK (성공 시 바디 없음)
     * @throws org.springframework.security.authentication.BadCredentialsException 일치하지 않으면
     */
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails currentUserDetails
    ) {
        User currentUser = currentUserDetails != null ? currentUserDetails.getUser() : null;
        updateService.verifyPassword(currentUser, body.get("password"));
        return ResponseEntity.ok().build();
    }

    /**
     * 프로필 정보를 수정한다.
     *
     * @param req         수정 내용
     * @return 저장 완료된 사용자 엔티티(혹은 마스킹된 DTO로 변경 가능)
     */
    @PutMapping("/edit")
    public User updateProfile(
            @Valid @RequestBody ProfileUpdateRequest req,
            @AuthenticationPrincipal CustomUserDetails currentUserDetails
    ) {
        User currentUser = currentUserDetails != null ? currentUserDetails.getUser() : null;
        return updateService.updateProfile(currentUser, req);
    }

    @GetMapping("/check/nickname")
    public boolean checkNicknameForEdit(
            @RequestParam String nickname,
            @RequestParam Long userIdx
    ) {
        return updateService.isNicknameDuplicateForEdit(nickname, userIdx);
    }
}
