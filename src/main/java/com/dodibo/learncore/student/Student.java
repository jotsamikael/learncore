package com.dodibo.learncore.student;

import com.dodibo.learncore.subscription.SubscriptionType;
import com.dodibo.learncore.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


/*
 * This class represents the candidate Entity which is also a user of the system
 *  and can therefore login, logout etc. like a normal user
 * */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "students")
public class Student extends User {

    @Column(nullable = false, length = 50)
    private String username;

    private int xp = 0;

    private int level = 1;

    private int streakDays = 0;

    private String refer_code;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
}
