package com.dodibo.learncore.elearningcore.question_option.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionOptionForm {

    @NotBlank
    private String questionUuid;

    @NotBlank
    private String optionText;

    private boolean correct;

    @Schema(type = "string", format = "binary", description = "Option image file")
    private org.springframework.web.multipart.MultipartFile image;
}
