package com.example.task_service.entity;

import com.example.task_service.entity.enums.Priority;
import com.example.task_service.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // from JWT

    private String title;

    @Column(length = 500)
    private String description; // optional

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Integer estimatedTimeMinutes;

    private LocalTime deadlineTime; // optional

    private LocalDate taskDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
