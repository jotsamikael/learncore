package com.dodibo.learncore.elearningcore.bookmark.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindBookmarkQuery extends PaginationQuery {

    private String questionUuid;
}
