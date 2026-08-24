package com.dodibo.learncore.elearningcore.category.dto;

import java.util.List;

public record CategoryResponse(
        String uuid,
        String name,
        String slug,
        String description,
        String imageUrl,
        String parentUuid,
        String parentName,
        String languageUuid,
        String languageCode
        //List<CategoryResponse> categoryResponses
) {
}
