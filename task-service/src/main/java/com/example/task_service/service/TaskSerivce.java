package com.example.task_service.service;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.dto.request.TaskUpdateRequestDTO;
import com.example.task_service.dto.response.TaskResponseDTO;
import com.example.task_service.entity.enums.TaskStatus;

import java.util.List;

public interface TaskSerivce {
    String createNewTask(TaskCreateRequestDTO taskCreateRequestDTO, int userId);

    List<TaskResponseDTO> getAllTasks(int userId);

    TaskResponseDTO getTaskById(Long taskId, int userId);

    List<TaskResponseDTO> getAllTasksByStatus(TaskStatus status, int userId);

    TaskResponseDTO updateTask(Long taskId, int userId, TaskUpdateRequestDTO taskUpdateRequestDTO);

    String delete(Long taskId, int userId);

    List<TaskResponseDTO> getTodayTasksByStatus(TaskStatus status, int userId);

    List<TaskResponseDTO> getOverdueTasks(int userId);

    void markTaskCompleted(Long id);
}
