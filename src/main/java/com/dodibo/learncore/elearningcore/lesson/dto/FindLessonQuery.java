package com.dodibo.learncore.elearningcore.lesson.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class FindLessonQuery extends PaginationQuery {

    private String title;
    private String categoryUuid;
    private Boolean isPremium;

    public FindLessonQuery() {
        setSortBy("position");
        setSortDirection(Sort.Direction.ASC);
    }

    @Override
    public Pageable toPageable() {
        if ("createdDate".equals(getSortBy())) {
            return PageRequest.of(getPage(), getSize(), Sort.by(Sort.Direction.ASC, "position"));
        }
        return super.toPageable();
    }
}
