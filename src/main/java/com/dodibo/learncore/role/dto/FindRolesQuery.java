package com.dodibo.learncore.role.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;

public class FindRolesQuery extends PaginationQuery {

    private String name;
    private String description;
    private String roleLevel; // matches RoleLevel enum as String for flexible binding

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }
}