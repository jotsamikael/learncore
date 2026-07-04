package com.dodibo.learncore.user;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue
    private Long idToken;
    private String token;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private LocalDateTime validatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TokenPurpose purpose = TokenPurpose.ACCOUNT_ACTIVATION;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}
