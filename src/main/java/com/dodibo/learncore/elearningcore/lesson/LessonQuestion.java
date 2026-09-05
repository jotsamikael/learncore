package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.question.Question;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "lesson_questions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "display_order"})
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LessonQuestion {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
