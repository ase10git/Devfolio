package io.github.sunday.devfolio.entity.table.profile;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 교육/트레이닝(Training) 정보를 나타내는 엔티티입니다.
 */
@Entity
@Table(name = "training")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Training {
    /** 교육 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_idx")
    private Long trainingIdx;

    /** 교육이 속한 이력서 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx", nullable = false)
    private Resume resume;

    /** 교육/트레이닝 제목 */
    @Column(name = "training_name", length = 200)
    private String trainingName;

    /** 시작 일자 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 종료 일자 */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** 상세 내용 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 객체 동등성 비교 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training that = (Training) o;
        return Objects.equals(trainingIdx, that.trainingIdx);
    }
    /** 해시코드계산 */
    @Override
    public int hashCode() {
        return Objects.hash(trainingIdx);
    }
}