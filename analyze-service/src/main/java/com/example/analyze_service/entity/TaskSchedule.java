package com.example.analyze_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_schedule")
@Data
public class TaskSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long analysisId;
    private Long taskId;

    private String title;        // Original title
    private String displayTitle; // Title with Part info

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean isBreak;

    private Integer partNumber;  // null if not split

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, COMPLETED
}