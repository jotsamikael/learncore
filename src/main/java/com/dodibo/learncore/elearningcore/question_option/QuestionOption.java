package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.question.Question;
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
public class QuestionOption extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "qo";
    }

    @Column(nullable = false, length = 256)
    private String optionText;

    @Column(nullable = false)
    private boolean correct;

    @Column(length = 256)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
