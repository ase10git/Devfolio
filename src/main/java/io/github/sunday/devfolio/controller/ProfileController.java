//package io.github.sunday.devfolio.controller;
//
//import io.github.sunday.devfolio.dto.ProfileDto;
//import io.github.sunday.devfolio.dto.ProfileUpdateRequest;
//import io.github.sunday.devfolio.entity.table.profile.Resume;
//import io.github.sunday.devfolio.entity.table.user.User;
//import io.github.sunday.devfolio.service.ProfileService;
//import io.github.sunday.devfolio.service.ProfileUpdateService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
///**
// * 프로필 화면/수정 기능을 제공하는 REST 컨트롤러.
// *
// * <p>엔드포인트 개요</p>
// * <ul>
// *   <li>GET /api/profile/{userId} : 프로필 헤더/요약 정보 조회</li>
// *   <li>POST /api/profile/{userId}/follow : 팔로우/언팔로우 토글</li>
// *   <li>GET /api/profile/{userId}/resumes : 이력서 목록 조회</li>
// *   <li>POST /api/profile/verify-password : 2차 인증(비밀번호 검증)</li>
// *   <li>PUT /api/profile/edit : 프로필 정보 수정</li>
// * </ul>
// *
// * <p>인증</p>
// * <ul>
// *   <li>{@code @AuthenticationPrincipal} 로 현재 사용자 정보를 주입받는다.</li>
// *   <li>프로덕션에서는 User 엔티티 대신 커스텀 UserDetails를 사용하는 구성이 일반적.</li>
// * </ul>
// */
//@RestController
//@RequestMapping("/api/profile")
//@RequiredArgsConstructor
//@Validated
//public class ProfileController {
//
//    private final ProfileService profileService;
//    private final ProfileUpdateService updateService;
//
//    /**
//     * 프로필 요약 정보를 조회한다.
//     *
//     * @param userId      대상 사용자 ID
//     * @param currentUser 현재 로그인 사용자
//     * @return 프로필 DTO
//     */
//    @GetMapping("/{userId}")
//    public ProfileDto getProfile(
//            @PathVariable Long userId,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        return profileService.getProfile(userId, currentUser);
//    }
//
//    /**
//     * 팔로우/언팔로우 상태를 토글한다.
//     *
//     * @param userId      대상 사용자 ID
//     * @param currentUser 현재 로그인 사용자
//     * @return {"following": true|false}
//     */
//    @PostMapping("/{userId}/follow")
//    public Map<String, Boolean> toggleFollow(
//            @PathVariable Long userId,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        boolean nowFollowing = profileService.toggleFollow(currentUser, userId);
//        return Collections.singletonMap("following", nowFollowing);
//    }
//
//    /**
//     * 대상 사용자의 이력서 목록을 조회한다.
//     *
//     * @param userId 대상 사용자 ID
//     * @return 이력서 리스트
//     */
//    @GetMapping("/{userId}/resumes")
//    public List<Resume> getResumes(
//            @PathVariable Long userId
//    ) {
//        return profileService.getResumes(userId);
//    }
//
//    /**
//     * 프로필 수정 전 비밀번호 재확인(2차 인증).
//     *
//     * @param body        {"password": "입력값"}
//     * @param currentUser 현재 로그인 사용자
//     * @return 200 OK (성공 시 바디 없음)
//     * @throws org.springframework.security.authentication.BadCredentialsException 일치하지 않으면
//     */
//    @PostMapping("/verify-password")
//    public ResponseEntity<?> verifyPassword(
//            @RequestBody Map<String, String> body,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        updateService.verifyPassword(currentUser, body.get("password"));
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 프로필 정보를 수정한다.
//     *
//     * @param req         수정 내용
//     * @param currentUser 현재 로그인 사용자
//     * @return 저장 완료된 사용자 엔티티(혹은 마스킹된 DTO로 변경 가능)
//     */
//    @PutMapping("/edit")
//    public User updateProfile(
//            @Valid @RequestBody ProfileUpdateRequest req,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        return updateService.updateProfile(currentUser, req);
//    }
//}
