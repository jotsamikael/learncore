package com.dodibo.learncore.tenant.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;

public class FindTenantsQuery extends PaginationQuery {
    private String name;
    private String phone;
    private String examFocus;
    private String country;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExamFocus() {
        return examFocus;
    }

    public void setExamFocus(String examFocus) {
        this.examFocus = examFocus;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
