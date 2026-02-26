package com.example.analyze_service.repo;

import com.example.analyze_service.entity.TaskSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskScheduleRepo extends JpaRepository<TaskSchedule, Long> {
}