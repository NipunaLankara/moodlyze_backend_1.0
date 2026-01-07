package com.example.auth_service.repo;

import com.example.auth_service.entity.AuthUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface AuthUserRepo  extends JpaRepository<AuthUsers,Integer> {
    boolean existsByUsername(String email);

    AuthUsers findByUsername(String email);
}
