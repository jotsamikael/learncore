package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import com.dodibo.learncore.elearningcore.language.dto.UpdateLanguageRequest;
import org.springframework.data.domain.Page;

public interface LanguageService {

    LanguageResponse createLanguage(CreateLanguageRequest createLanguageRequest);

    Page<LanguageResponse> getLanguages(FindLanguageQuery query);

    LanguageResponse getLanguage(String uuid);

    LanguageResponse updateLanguage(String uuid, UpdateLanguageRequest request);

    void deleteLanguage(String uuid);
}
