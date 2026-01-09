package com.example.task_service.repo;

import com.example.task_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(int userId);
}
