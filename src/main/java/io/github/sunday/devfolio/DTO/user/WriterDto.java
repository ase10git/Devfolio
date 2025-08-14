package io.github.sunday.devfolio.dto.user;

import lombok.*;

/**
 * 포트폴리오, 게시글, 댓글 작성자에 대한 응답용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriterDto {

    /**
     * 사용자 고유 식별자 (PK)
     */
    private Long userIdx;

    /**
     * 사용자 닉네임 (고유, 필수)
     */
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    private String profileImg;
}
