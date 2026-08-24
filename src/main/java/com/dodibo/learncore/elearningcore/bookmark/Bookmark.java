package com.dodibo.learncore.elearningcore.bookmark;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
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
public class Bookmark extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "bk";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
