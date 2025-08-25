package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.ZonedDateTime;

/**
 * 게시글 생성을 요청할 때 사용하는 DTO.
 */
@Getter
@Setter
@NoArgsConstructor // 기본 생성자도 필요합니다.
public class PostCreateRequestDto {
    private String title;

    private String content;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    private String status = "ACTIVE";

    public void setContent(String content) {
        if (content != null) {
            this.content = Jsoup.clean(content, Safelist.relaxed());
        } else {
            this.content = null;
        }
    }

    /**
     * DTO를 CommunityPost 엔티티로 변환합니다.
     * @param user 게시글을 작성하는 사용자
     * @return 변환된 CommunityPost 엔티티
     */
    public CommunityPost toEntity(User user) {
        return CommunityPost.builder()
                .user(user)
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .status(this.status)
                .views(0)
                .likeCount(0)
                .commentCount(0)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }
}