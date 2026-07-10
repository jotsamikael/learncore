package com.dodibo.learncore.staff.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;

public class FindStaffQuery extends PaginationQuery {
    private String firstname;
    private String email;
    private String lastname;
    private String positionName;


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}
