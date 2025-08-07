package io.github.sunday.devfolio.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 학력(Education) 정보를 관리하는 엔티티입니다.
 */
@Entity
@Table(name = "education")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Education {
    /** 학력 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edu_idx")
    private Long eduIdx;

    /** 학력 정보가 속한 이력서 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx", nullable = false)
    private Resume resume;

    /** 학교명 */
    @Column(name = "school", length = 200)
    private String school;

    /** 학위/과정 */
    @Column(name = "degree", length = 100)
    private String degree;

    /** 전공 */
    @Column(name = "majored_in", length = 100)
    private String majoredIn;

    /** 시작 일자 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 종료 일자 */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** 평점(Grade) */
    @Column(name = "grade")
    private Integer grade;

    /** 상세 설명 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 레코드 생성 일자 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    /** 객체 동등성 비교 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Education that = (Education) o;
        return Objects.equals(eduIdx, that.eduIdx);
    }
    /** 해시코드계산 */
    @Override
    public int hashCode() {
        return Objects.hash(eduIdx);
    }
}

