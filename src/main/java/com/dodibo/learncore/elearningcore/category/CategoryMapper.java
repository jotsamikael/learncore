package com.dodibo.learncore.elearningcore.category;

import com.dodibo.learncore.elearningcore.category.dto.CategoryResponse;
import com.dodibo.learncore.elearningcore.category.dto.CreateCategoryRequest;
import com.dodibo.learncore.elearningcore.category.dto.UpdateCategoryRequest;
import com.dodibo.learncore.elearningcore.language.Language;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return toResponse(category, List.of());
    }

    public CategoryResponse toResponse(Category category, List<CategoryResponse> categoryResponses) {
        if (category == null) {
            return null;
        }
        Category parent = category.getParent();
        Language language = category.getLanguage();
        return new CategoryResponse(
                category.getUuid(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                parent != null ? parent.getUuid() : null,
                parent != null ? parent.getName() : null,
                language != null ? language.getUuid() : null,
                language != null ? language.getCode() : null
                //categoryResponses == null ? List.of() : categoryResponses
        );
    }

    public Category toEntity(CreateCategoryRequest request, Long tenantId, Category parent, Language language, String imageUrl) {
        if (request == null) {
            return null;
        }
        return Category.builder()
                .tenantId(tenantId)
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .imageUrl(imageUrl)
                .parent(parent)
                .language(language)
                .build();
    }

    public void applyUpdate(Category category, UpdateCategoryRequest request, Category parent, Language language) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setParent(parent);
        category.setLanguage(language);
    }
}
