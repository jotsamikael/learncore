package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.FindStaffQuery;
import org.springframework.data.jpa.domain.Specification;

public class StaffSpecification {

    public StaffSpecification() {
    }

    public static Specification<Staff> fromQuery(FindStaffQuery query, Long tenantId) {
        return Specification
                .where(belongsToTenant(tenantId))
                .and(hasFirstname(query.getFirstname()))
                .and(hasLastname(query.getLastname()))
                .and(hasEmail(query.getEmail()))
                .and(hasPosition(query.getPositionName()));
    }


    private static Specification<Staff> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> tenantId == null
                ? cb.isNull(root.get("tenant"))
                : cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<Staff> hasFirstname(String firstname) {
        return (root, cq, cb) -> firstname == null || firstname.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    private static Specification<Staff> hasLastname(String lastname) {
        return (root, cq, cb) -> lastname == null || lastname.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }


    private static Specification<Staff> hasEmail(String email) {
        return (root, cq, cb) -> email == null || email.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<Staff> hasPosition(String position) {
        return (root, cq, cb) -> position == null || position.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("positionName")), "%" + position.toLowerCase() + "%");
    }
}
