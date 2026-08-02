package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.FindCategoriesQuery;
import com.dodibo.learncore.elearningcore.language.Language;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> fromQuery(FindCategoriesQuery query) {
        return Specification.where(hasName(query.getName()))
                .and(hasDescription(query.getDescription()));
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
