package com.example.auth_service.repo;

import com.example.auth_service.entity.AuthUsers;
import com.example.auth_service.entity.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface LoginActivityRepo extends JpaRepository<LoginActivity, Long> {

    List<LoginActivity> findTop5ByAuthUserIdOrderByLoginTimeDesc(AuthUsers authUserId);
}
