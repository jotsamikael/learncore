package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "lesson",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "position"})
)
@EntityListeners(AuditingEntityListener.class)
public class Lesson extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "ls";
    }

    // Mandatory Relationships
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Entity Attributes
    @NotNull
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    //this variable determines the position of this lesson in the learning hierarchy
    @NotNull
    @Column(name="position", nullable = false)
    private int position;

    //this is the estimated number of minutes required to complete this lesson
    @NotNull
    @Column(name="completion_time")
    private int completionTime;


    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;
}
