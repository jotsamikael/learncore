package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.common.BaseEntity;
import com.dodibo.learncore.tenant.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Language extends BaseEntity {
    @Override
    protected String uuidPrefix() {
        return "la";
    }

    @Column(nullable = false, length = 100, unique = false)
    private String name;

    @Column(nullable = false, unique = false, length = 6)
    private String code;

    @Column(nullable = false)
    private Long tenantId;

}
