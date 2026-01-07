package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", length = 255)
    private int userId;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "contact_number", length = 300, nullable = false)
    private String contactNumber;

    @Column(name = "user_role", length = 150, nullable = false)
    private String userRole;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;



    // Automatically set createdAt before inserting
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }


    public User(String name, String email, String address, String contactNumber, String userRole) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.contactNumber = contactNumber;
        this.userRole = userRole;
    }
}
