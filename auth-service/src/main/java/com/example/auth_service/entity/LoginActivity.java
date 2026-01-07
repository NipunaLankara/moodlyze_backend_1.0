package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "login_activity")
public class LoginActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_time", updatable = false)
    private Instant loginTime;

    @Column(name = "notified")
    private boolean notified = false;

//    @Column(name = "status", nullable = false, length = 20)
//    private String status; // SUCCESS / FAILED

    // Map to AuthUser entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUsers authUserId;

    @PrePersist
    protected void onCreate() {
        this.loginTime = Instant.now();
    }

}
