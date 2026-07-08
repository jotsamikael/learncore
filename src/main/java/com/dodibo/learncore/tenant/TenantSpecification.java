package com.dodibo.learncore.tenant;

import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.role.dto.FindRolesQuery;
import com.dodibo.learncore.tenant.dto.FindTenantsQuery;
import org.springframework.data.jpa.domain.Specification;

public class TenantSpecification {

    public TenantSpecification() {
    }

    public static Specification<Tenant> fromQuery(FindTenantsQuery query) {
        return Specification
                .where(hasName(query.getName()))
                .and(hasPhone(query.getPhone()))
                .and(hasExamFocus(query.getExamFocus()))
                .and(hasCountry(query.getCountry()))
                .and(hasDescription(query.getDescription()));
    }

    private static Specification<Tenant> hasName(String name) {
        return (root, cq, cb) -> name == null || name.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Tenant> hasPhone(String phone) {
        return (root, cq, cb) -> phone == null || phone.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }

    private static Specification<Tenant> hasExamFocus(String examFocus) {
        return (root, cq, cb) -> examFocus == null || examFocus.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("examFocus")), "%" + examFocus.toLowerCase() + "%");
    }

    private static Specification<Tenant> hasCountry(String country) {
        return (root, cq, cb) -> country == null || country.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%");
    }

    private static Specification<Tenant> hasDescription(String description) {
        return (root, cq, cb) -> description == null || description.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}
