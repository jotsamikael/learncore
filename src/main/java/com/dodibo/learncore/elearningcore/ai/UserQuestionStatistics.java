package com.dodibo.learncore.elearningcore.ai;

import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_question_stats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_question_tenant",
                        columnNames = {"tenant_id", "user_id", "question_id"}
                )
        },
        indexes = {
                @Index(name = "idx_uqs_tenant", columnList = "tenant_id"),
                @Index(name = "idx_uqs_review", columnList = "tenant_id, user_id, next_review_at"),
                @Index(name = "idx_uqs_mastery", columnList = "tenant_id, user_id, mastery_score")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestionStatistics {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "correct_attempts", nullable = false)
    private Integer correctAttempts = 0;

    @Column(name = "average_time_sec", precision = 6, scale = 2)
    private BigDecimal averageTimeSec;

    @Column(name = "mastery_score", precision = 5, scale = 4)
    private BigDecimal masteryScore = new BigDecimal("0.0000");

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;

    @Column(name = "sm2_interval")
    private Integer sm2Interval = 1;

    @Column(name = "sm2_easiness", precision = 4, scale = 2)
    private BigDecimal sm2Easiness = new BigDecimal("2.50");

    @Column(name = "sm2_repetitions")
    private Integer sm2Repetitions = 0;

    @Column(name = "last_attempted_at")
    private LocalDateTime lastAttemptedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
