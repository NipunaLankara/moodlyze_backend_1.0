package com.example.task_service.repo;

import com.example.task_service.entity.Task;
import com.example.task_service.entity.enums.TaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(int userId);

    Optional<Task> findByIdAndUserId(Long taskId, int userId);

    List<Task> findByStatusAndUserId(TaskStatus status, int userId);


}
