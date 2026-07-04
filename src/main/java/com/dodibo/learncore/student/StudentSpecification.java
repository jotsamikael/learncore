package com.dodibo.learncore.student;

import com.dodibo.learncore.student.dto.FindStudentsQuery;
import org.springframework.data.jpa.domain.Specification;

public final class StudentSpecification {

    private StudentSpecification() {
    }

    public static Specification<Student> fromQuery(FindStudentsQuery query) {
        return Specification
                .where(hasFirstname(query.getFirstname()))
                .and(hasLastname(query.getLastname()))
                .and(hasUsername(query.getUsername()))
                .and(hasEmail(query.getEmail()))
                .and(hasXp(query.getXp()))
                .and(hasStreakDays(query.getStreakDays()))
                .and(hasLevel(query.getLevel()));
    }

    public static Specification<Student> hasTenantId(Long tenantId) {
        return (root, cq, cb) -> tenantId == null
                ? cb.conjunction()
                : cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<Student> hasFirstname(String firstname) {
        return (root, cq, cb) -> firstname == null || firstname.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    private static Specification<Student> hasLastname(String lastname) {
        return (root, cq, cb) -> lastname == null || lastname.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }

    private static Specification<Student> hasUsername(String username) {
        return (root, cq, cb) -> username == null || username.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    private static Specification<Student> hasEmail(String email) {
        return (root, cq, cb) -> email == null || email.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<Student> hasXp(int xp) {
        return (root, cq, cb) -> xp <= 0
                ? cb.conjunction()
                : cb.equal(root.get("xp"), xp);
    }

    private static Specification<Student> hasStreakDays(int streakDays) {
        return (root, cq, cb) -> streakDays <= 0
                ? cb.conjunction()
                : cb.equal(root.get("streakDays"), streakDays);
    }

    private static Specification<Student> hasLevel(int level) {
        return (root, cq, cb) -> level <= 0
                ? cb.conjunction()
                : cb.equal(root.get("level"), level);
    }
}
