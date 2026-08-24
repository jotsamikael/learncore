package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.elearningcore.language.Language;
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
public class Category extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "ca";
    }

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String slug;

    @ManyToOne(fetch = FetchType.EAGER) //Many categories can be linked to a category
    @JoinColumn(name = "parent_id")
    private Category parent;

    @ManyToOne(fetch = FetchType.LAZY) //Many categories can be linked to a category
    @JoinColumn(name = "language_id")
    private Language language;

    private String description;

    private String imageUrl;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private java.util.List<Category> children = new java.util.ArrayList<>();

}
