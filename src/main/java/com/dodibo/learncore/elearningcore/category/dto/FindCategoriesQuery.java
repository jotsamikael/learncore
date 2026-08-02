package com.dodibo.learncore.elearningcore.category.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindCategoriesQuery extends PaginationQuery {

    private String name;
    private String description;

}
