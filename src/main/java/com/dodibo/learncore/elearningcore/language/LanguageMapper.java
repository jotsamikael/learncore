package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper {

    public LanguageResponse toResponse(Language language){
        if(language == null){
            return null;
        }
        return new LanguageResponse(
                language.getUuid(),
                language.getName(),
                language.getCode()
        );
    }

    public Language toEntity(CreateLanguageRequest createLanguageRequest, Long tenantId){
        if(createLanguageRequest == null){
            return null;
        }
       return Language.builder().code(createLanguageRequest.code()).name(createLanguageRequest.name()).tenantId(tenantId).build();
    }
}
