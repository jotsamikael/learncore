package com.dodibo.learncore.elearningcore.session;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.daily_quiz.DailyQuiz;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.session.enums.SessionType;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class QuizSession extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "qs";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private SessionType sessionType;


    // Foreign Keys / Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_quiz_id")
    private DailyQuiz dailyQuiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_quiz_id")
    private WeeklyQuiz weeklyQuiz;

    // Timestamps
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Metrics & Performance
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    @Column(name = "score_percentage", precision = 5, scale = 2)
    private BigDecimal scorePercentage;

    @Column(name = "xp_earned", nullable = false)
    private Integer xpEarned = 0;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

}
