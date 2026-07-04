package com.dodibo.learncore.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class PaginationQuery {

    private int page = 0;
    private int size = 20;
    private String sortBy = "createdDate";
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    public int getPage() {
        return Math.max(page, 0);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size < 1 ? 20 : Math.min(size, 100); // cap page size to prevent abuse
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Pageable toPageable() {
        return PageRequest.of(getPage(), getSize(), Sort.by(sortDirection, sortBy));
    }
}
