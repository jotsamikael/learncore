package com.dodibo.learncore.permission;

import com.dodibo.learncore.common.UuidGenerator;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.role.RoleLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
/*
* This class represents a permission in the system.
* A permission is a capability that a user can have.
* For example, a user can have the permission to read a resource.
* The permission is associated with a role.
* The permission is associated with a user.
* The permission is associated with a tenant.
* The permission is associated with a resource.
* The permission is associated with a resource type.
* The permission is associated with a resource action.
* */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid;

    @PrePersist
    private void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UuidGenerator.generate("pm");
        }
    }

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleLevel level;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
