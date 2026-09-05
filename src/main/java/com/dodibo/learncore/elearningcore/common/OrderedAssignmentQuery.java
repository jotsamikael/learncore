package com.dodibo.learncore.elearningcore.common;

import com.dodibo.learncore.common.dto.PaginationQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class OrderedAssignmentQuery extends PaginationQuery {

    @Override
    public Pageable toPageable() {
        if ("createdDate".equals(getSortBy())) {
            return PageRequest.of(getPage(), getSize(), Sort.by(Sort.Direction.ASC, "displayOrder"));
        }
        return super.toPageable();
    }
}
