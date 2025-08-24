package io.github.sunday.devfolio.dto.user;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 프로필 수정 요청 페이로드.
 *
 * 비밀번호는 선택 입력이며, 값이 있을 때만 변경한다.
 * 이메일 및 닉네임은 중복 불가 정책을 서비스에서 검증한다.
 */
@Data
public class ProfileUpdateRequest {

    /** 이메일 (필수, 형식 검증) */
    @Email
    @NotBlank
    private String email;

    /** 닉네임 (필수) */
    @NotBlank
    private String nickname;

    /** 새 비밀번호 (선택) */
    private String password;

    /** GitHub 주소 (선택) */
    private String githubUrl;

    /** 블로그 주소 (선택) */
    private String blogUrl;

    /** 소속 (선택) */
    private String affiliation;

    /** 이미지(별도 처리시 URL 로 받는형태) */
    private String profileImg;
}
