package com.example.analyze_service.repo;

import com.example.analyze_service.entity.TaskAnalysis;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories

public interface AnalysisRepo extends CrudRepository<TaskAnalysis, Long> {
}
