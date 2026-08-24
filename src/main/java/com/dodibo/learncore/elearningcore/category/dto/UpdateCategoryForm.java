package com.dodibo.learncore.elearningcore.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateCategoryForm {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String slug;

    @NotNull
    @NotBlank
    private String description;

    private String parentUuid;

    private String languageUuid;

    @Schema(type = "string", format = "binary", description = "Category image file")
    private MultipartFile image;

    public UpdateCategoryRequest toRequest() {
        return new UpdateCategoryRequest(name, slug, description, parentUuid, languageUuid);
    }
}
