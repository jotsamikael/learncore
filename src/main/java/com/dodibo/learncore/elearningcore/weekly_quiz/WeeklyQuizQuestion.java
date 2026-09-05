package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.question.Question;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "weekly_quiz_questions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"weekly_quiz_id", "display_order"})
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WeeklyQuizQuestion {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_quiz_id", nullable = false)
    private WeeklyQuiz weeklyQuiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
