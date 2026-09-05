package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.question.enums.GradingStrategy;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//@ This entity contains the reference answer and instructions for grading.
@Entity
@Table(name = "written_answer_configs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrittenAnswerConfig extends BaseEntity {

    @Override
    protected String uuidPrefix() {
        return "wac";
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    /**
     * The ideal/reference answer provided by the question creator.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String referenceAnswer;

    /**
     * Maximum score available for this question.
     */
    @Column(nullable = false)
    private BigDecimal maxScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GradingStrategy gradingStrategy;

    /**
     * Useful mainly for structural answers.
     * Example: 0.70 means 70% similarity is required.
     */
    private Double minimumScoreThreshold;

    @OneToMany(
            mappedBy = "writtenAnswerConfig",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GradingCriterion> gradingCriteria = new ArrayList<>();
}
