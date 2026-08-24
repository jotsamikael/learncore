package com.dodibo.learncore.elearningcore.leaderboard;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.leaderboard.enums.LeaderboardType;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LeaderboardEntry extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "le";
    }


    // Foreign Keys / Mandatory Relationships
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Enum Mapping
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "leaderboard_type", nullable = false)
    private LeaderboardType leaderboardType;

    // Optional Foreign Keys / Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_quiz_id")
    private WeeklyQuiz weeklyQuiz;

    // Scores & Rankings
    @Column(name = "score", precision = 6, scale = 2)
    private BigDecimal score;

    @Column(name = "rank_position")
    private Integer rankPosition;
}
