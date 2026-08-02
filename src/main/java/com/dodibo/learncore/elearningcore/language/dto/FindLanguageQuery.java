package com.dodibo.learncore.elearningcore.language.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindLanguageQuery extends PaginationQuery {
    private String name;
    private String code;


}
