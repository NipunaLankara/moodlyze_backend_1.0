package com.example.analyze_service.repo;

import com.example.analyze_service.entity.TaskAnalysis;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@EnableJpaRepositories

public interface AnalysisRepo extends CrudRepository<TaskAnalysis, Long> {
    Optional<TaskAnalysis> findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            int userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
