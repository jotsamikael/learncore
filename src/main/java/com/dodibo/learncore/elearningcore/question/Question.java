package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Question extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "qu";
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionType questionType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(length = 512)
    private String explanation;

    @Column(length = 256)
    private String imageUrl;

    @Column(nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @OneToOne(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private WrittenAnswerConfig writtenAnswerConfig;
}
