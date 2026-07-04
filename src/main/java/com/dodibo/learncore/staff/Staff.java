package com.dodibo.learncore.staff;

import com.dodibo.learncore.common.UuidGenerator;
import com.dodibo.learncore.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "staff")
@EntityListeners(AuditingEntityListener.class)
public class Staff extends User {

    private String positionName;

    @PrePersist
    private void generateStaffUuid() {
        if (this.getUuid() == null) {
            this.setUuid(UuidGenerator.generate("st"));
        }
    }

    public boolean isPlatformStaff() {
        return isPlatformUser();
    }

    public boolean isTenantStaff() {
        return !isPlatformUser();
    }
}
