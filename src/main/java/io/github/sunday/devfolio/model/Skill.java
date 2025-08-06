package io.github.sunday.devfolio.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 스킬(사용 언어 및 툴)(Skill) 정보를 저장하는 엔티티.
 */
@Entity
@Table(name = "skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Skill {
    /** 스킬 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_idx")
    private Long skillIdx;

    /** 스킬이 속한 이력서 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx", nullable = false)
    private Resume resume;

    /** 스킬 이름 */
    @Column(name = "name", length = 200)
    private String name;

    /** 숙련도 레벨 (예: 1~5) */
    @Column(name = "level")
    private Integer level;

    /** 시작 일자 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 종료 일자 */
    @Column(name = "end_date")
    private LocalDate endDate;
}
