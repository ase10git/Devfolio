package io.github.sunday.devfolio.entity.table.profile;

import io.github.sunday.devfolio.entity.table.user.User;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자가 작성한 이력서(Resume) 정보를 관리하는 엔티티.
 * 본문, 첨부파일, 경력/학력/스킬/교육 정보와 연관됩니다.
 */
@Entity
@Table(name = "resumes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resume {
    /** 이력서 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_idx")
    private Long resumeIdx;

    /** 이력서 작성자 (User) 참조 */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_idx", nullable = false)
//    private User user;

    /** 이력서 본문 내용 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 이력서 생성 일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 이력서 업데이트 일시 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 프로필 요약 또는 대표 이미지 URL */
    @Column(name = "profile", length = 255)
    private String profile;


    /** 객체 동등성 비교 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(resumeIdx, resume.resumeIdx);
    }
    /** 해시코드계산 */
    @Override
    public int hashCode() {
        return Objects.hash(resumeIdx);
    }
}

