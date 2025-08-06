package io.github.sunday.devfolio.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 이력서 첨부 파일 정보를 나타내는 엔티티입니다.
 */
@Entity
@Table(name = "resume_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeFile {
    /** 파일 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_idx")
    private Long fileIdx;

    /** 첨부 대상 이력서 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx", nullable = false)
    private Resume resume;

    /** 파일명 */
    @Column(name = "filename", length = 255)
    private String filename;

    /** 파일 저장 URL */
    @Column(name = "file_url", length = 512)
    private String fileUrl;

    /** 파일 업로드 일시 */
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
