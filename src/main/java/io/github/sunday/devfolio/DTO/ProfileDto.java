package io.github.sunday.devfolio.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * 프로필 조회 응답 정보
 */
@Data
@Builder
public class ProfileDto {
    private Long userId;
    private String nickname;
    private String email;
    private String profileImg;
    private String githubUrl;
    private String blogUrl;
    private String affiliation;
    private long followingCount;
    private long followerCount;
    private boolean isFollowing;
}