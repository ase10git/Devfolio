package io.github.sunday.devfolio.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 경력(Experience) 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "experiences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Experience {
    /** 경력 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_idx")
    private Long expIdx;

    /** 경력 정보가 속한 이력서 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx", nullable = false)
    private Resume resume;

    /** 회사명 */
    @Column(name = "company", length = 200)
    private String company;

    /** 직무 포지션 */
    @Column(name = "position", length = 200)
    private String position;

    /** 시작 일자 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 종료 일자 */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** 상세 설명 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 레코드 생성 일자 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
}