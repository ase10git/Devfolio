package io.github.sunday.devfolio.dto;

import io.github.sunday.devfolio.entity.table.user.AuthProvider;
import lombok.Builder;
import lombok.Data;

/**
 * 프로필 조회 응답 정보
 */
@Data
@Builder
public class ProfileDto {

    /** 사용자 고유 식별자 */
    private Long userIdx;

    /** 닉네임(표시명) */
    private String nickname;

    /** 이메일(표시/연락 목적) */
    private String email;

    /** OAuth 제공자 */
    private AuthProvider oauthProvider;

    /** 프로필 이미지 URL */
    private String profileImg;

    /** GitHub 프로필 URL */
    private String githubUrl;

    /** 블로그 주소 */
    private String blogUrl;

    /** 소속(회사/학교 등) */
    private String affiliation;

    /** 내가 팔로우하는 사람 수 */
    private long followingCount;

    /** 나를 팔로우하는 사람 수 */
    private long followerCount;

    /** (현재 로그인 사용자 기준) 팔로우 여부 */
    private boolean isFollowing;
}