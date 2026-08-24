package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {

    private CategorySpecification() {
    }

    public static Specification<Category> fromQuery(FindCategoriesQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasName(query.getName()))
                .and(hasDescription(query.getDescription()));
    }

    private static Specification<Category> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    private static Specification<Category> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Category> hasName(String name) {
        return (root, cq, cb) -> name == null || name.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Category> hasDescription(String description) {
        return (root, cq, cb) -> description == null || description.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}
