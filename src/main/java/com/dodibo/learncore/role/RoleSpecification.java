package com.dodibo.learncore.role;

import com.dodibo.learncore.role.dto.FindRolesQuery;
import org.springframework.data.jpa.domain.Specification;

/*
* role/RoleSpecification.java — dynamic filtering with JPA Specifications
* */
public final class RoleSpecification {

    private RoleSpecification() {
    }

    public static Specification<Role> fromQuery(FindRolesQuery query) {
        return Specification
                .where(hasName(query.getName()))
                .and(hasDescription(query.getDescription()));
    }

    public static Specification<Role> hasLevel(RoleLevel level) {
        return (root, cq, cb) -> cb.equal(root.get("level"), level);
    }

    public static Specification<Role> platformRoles() {
        return (root, cq, cb) -> cb.and(
                cb.equal(root.get("level"), RoleLevel.PLATFORM),
                cb.isNull(root.get("tenant"))
        );
    }

    public static Specification<Role> visibleToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.and(
                cb.equal(root.get("level"), RoleLevel.TENANT),
                cb.or(
                        cb.isNull(root.get("tenant")),
                        cb.equal(root.get("tenant").get("id"), tenantId)
                )
        );
    }

    private static Specification<Role> hasName(String name) {
        return (root, cq, cb) -> name == null || name.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Role> hasDescription(String description) {
        return (root, cq, cb) -> description == null || description.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}