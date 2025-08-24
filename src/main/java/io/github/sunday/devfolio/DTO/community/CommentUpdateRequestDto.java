package io.github.sunday.devfolio.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * 댓글 수정을 요청할 때 사용하는 DTO.
 */
@Getter
@Setter
public class CommentUpdateRequestDto {
    private Long commentId;

    @NotBlank(message = "잘못된 댓글 형식입니다.")
    private String content;

    public void setContent(String content) {
        if (content != null) {
            this.content = Jsoup.clean(content, Safelist.relaxed()).trim();
        } else {
            this.content = null;
        }
    }
}
