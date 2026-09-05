package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

//@This is particularly useful for essays.
@Entity
@Table(name = "grading_criteria")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingCriterion extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "gc";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "written_answer_config_id", nullable = false)
    private WrittenAnswerConfig writtenAnswerConfig;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Number of points given for satisfying this criterion.
     */
    @Column(nullable = false)
    private BigDecimal score;

    /**
     * Optional concepts / keywords that can help automated grading.
     */
    @ElementCollection
    @CollectionTable(
            name = "grading_criterion_keywords",
            joinColumns = @JoinColumn(name = "grading_criterion_id")
    )
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();
}
