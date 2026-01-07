package com.example.auth_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "auth_users")
public class AuthUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "user_name")  // user email
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "opt_status")
    private String optStatus="PENDING_OTP";   // PENDING_OTP,VERIFY

    @Column(name = "is_active")
    private boolean acvtiveStatus=false;   // false,true

    @OneToMany(mappedBy = "authUserId")
    @JsonIgnore
    private Set<LoginActivity> loginActivities;


    public AuthUsers(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
