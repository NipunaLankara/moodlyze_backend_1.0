package com.example.analyze_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_analyses")
@Data
public class TaskAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int userId;
    private String moodAtTime;

    @Column(columnDefinition = "TEXT")
    private String smartPlan; // Stores the detailed schedule & task breakdowns

    private String workingWindow; // e.g., "08:00 - 17:00"
    private LocalDateTime createdAt;
}