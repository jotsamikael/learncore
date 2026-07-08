The `RoleSpecification` class uses **Spring Data JPA Specifications** (built on top of the JPA Criteria API) to build database queries dynamically. Instead of writing static SQL or JPQL queries, it allows your application to combine different search filters and visibility rules at runtime based on the user's input and their context.

Here is a breakdown of how the class works, section by section.

---

## 1. Core Mechanics: The Functional Interface

A JPA `Specification<T>` is a functional interface that looks like this:

```java
public interface Specification<T> {
    Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
}

```

* **`root`**: Represents the entity you are querying (`Role` in this case). You use it to access paths/columns like `root.get("name")`.
* **`query`**: The overall criteria query container (used for clauses like `DISTINCT`, `ORDER BY`, etc.).
* **`criteriaBuilder`**: The factory used to construct the database predicates/conditions (e.g., `equal`, `like`, `isNull`, `and`, `or`).

---

## 2. Dynamic Filtering Methods

These methods filter roles based on user search input or specific criteria.

### `hasName` and `hasDescription`

These helper methods use a **case-insensitive partial match (`LIKE %value%`)**:

```java
private static Specification<Role> hasName(String name) {
    return (root, cq, cb) -> name == null || name.isBlank()
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
}

```

* **`cb.conjunction()`**: If the search parameter is empty, it returns a blank "always true" condition (`1=1`), meaning this specific filter is ignored and won't restrict the results.
* **`cb.like(...)`**: If a value is provided, it converts both the database column value and the search string to lowercase using `cb.lower()` to perform a case-insensitive `LIKE` search.

### `fromQuery`

This combines individual search criteria into a single compound specification:

```java
public static Specification<Role> fromQuery(FindRolesQuery query) {
    return Specification
            .where(hasName(query.getName()))
            .and(hasDescription(query.getDescription()));
}

```

* It safely chains the `name` and `description` predicates together using an `AND` clause via `Specification.where().and()`.

---

## 3. Security & Multi-Tenancy Rules

These methods are crucial because they ensure users only see the roles they are authorized to access, preventing tenants from seeing each other's custom roles.

### `platformRoles()`

Used when a platform-level administrator requests a list of roles.

```java
public static Specification<Role> platformRoles() {
    return (root, cq, cb) -> cb.and(
            cb.equal(root.get("level"), RoleLevel.PLATFORM),
            cb.isNull(root.get("tenant"))
    );
}

```

* Generates SQL equivalent to: `WHERE level = 'PLATFORM' AND tenant_id IS NULL`.

### `visibleToTenant(Long tenantId)`

Enforces boundaries within a multi-tenant model when a tenant administrator or staff views roles.

```java
public static Specification<Role> visibleToTenant(Long tenantId) {
    return (root, cq, cb) -> cb.and(
            cb.equal(root.get("level"), RoleLevel.TENANT),
            cb.or(
                    cb.isNull(root.get("tenant")),
                    cb.equal(root.get("tenant").get("id"), tenantId)
            )
    );
}

```

* It ensures that the role level is `TENANT`.
* It allows the tenant to see two types of roles:
1. **Built-in Tenant Roles:** Roles where `tenant_id IS NULL` but are flagged at the `TENANT` level (default system templates for all tenants).
2. **Custom Tenant Roles:** Roles explicitly created by and for that specific tenant (`tenant_id = :tenantId`).



---

## 4. How It Ties Together in `RoleServiceImpl`

When `getRoles(FindRolesQuery query)` is called, the service layers these specifications on top of each other:

```java
// 1. Determine user context visibility (Platform vs Specific Tenant)
Specification<Role> visibilitySpec = buildVisibilitySpec(assignableLevel);

// 2. Combine user's search inputs with the visibility restrictions
Specification<Role> spec = RoleSpecification.fromQuery(query).and(visibilitySpec);

// 3. Execute the final combined dynamic query using Spring Data JPA
return roleRepository.findAll(spec, query.toPageable()).map(roleMapper::toResponse);

```

This guarantees that the generated database query seamlessly includes both the **functional filters** (searching by name/description) and the **security constraints** (tenant isolation) in one clean database trip.