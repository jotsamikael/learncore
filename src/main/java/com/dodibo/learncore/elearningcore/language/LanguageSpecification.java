package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.student.Student;
import com.dodibo.learncore.student.dto.FindStudentsQuery;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;


@NoArgsConstructor
public final class LanguageSpecification {

    public static Specification<Language> fromQuery(FindLanguageQuery query, Long tenantId) {
        return Specification
                .where(belongsToTenant(tenantId))
                .and(hasName(query.getName()))
                .and(hasCode(query.getCode()));
    }

    private static Specification<Language> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> tenantId == null
                ? cb.isNull(root.get("tenantId"))
                : cb.equal(root.get("tenantId"), tenantId);
    }

    private static Specification<Language> hasName(String name) {
        return (root, cq, cb) -> name == null || name.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Language> hasCode(String code) {
        return (root, cq, cb) -> code == null || code.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }
}
