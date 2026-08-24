package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.tenant.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class WeeklyQuiz extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "wq";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;
}
