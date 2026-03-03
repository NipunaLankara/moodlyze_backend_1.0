package com.example.analyze_service.repo;

import com.example.analyze_service.entity.TaskSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskScheduleRepo extends JpaRepository<TaskSchedule, Long> {
    List<TaskSchedule> findByAnalysisIdOrderByStartTimeAsc(Long id);

    List<TaskSchedule> findByAnalysisIdAndStatusOrderByStartTimeAsc(Long id, String pending);

    List<TaskSchedule> findByTaskIdAndStatus(Long taskId, String pending);
}