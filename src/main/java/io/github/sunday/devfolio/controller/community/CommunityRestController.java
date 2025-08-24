package io.github.sunday.devfolio.controller.community;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.community.CommentUpdateRequestDto;
import io.github.sunday.devfolio.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community") // [중요] 경로를 /api/community로 변경하여 일반 요청과 분리
@RequiredArgsConstructor
public class CommunityRestController {
    private final CommunityService communityService;

    /**
     * 댓글 수정을 처리하는 API.
     *
     * @param requestDto        수정할 댓글 정보 (JSON)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 성공 시 HTTP 200 OK, 실패 시 에러 응답
     */
    @PutMapping("/comments")
    public ResponseEntity<String> updateComment(@Valid @RequestBody CommentUpdateRequestDto requestDto,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            communityService.updateComment(requestDto, customUserDetails.getUser().getUserIdx());
            return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 댓글 삭제를 처리하는 API.
     *
     * @param commentId         삭제할 댓글의 ID
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 성공 시 HTTP 200 OK, 실패 시 에러 응답
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            communityService.deleteComment(commentId, customUserDetails.getUser().getUserIdx());
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
