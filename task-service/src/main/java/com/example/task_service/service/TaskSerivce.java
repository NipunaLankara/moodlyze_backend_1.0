package com.example.task_service.service;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.dto.response.TaskResponseDTO;

import java.util.List;

public interface TaskSerivce {
    String createNewTask(TaskCreateRequestDTO taskCreateRequestDTO, int userId);

    List<TaskResponseDTO> getAllTasks(int userId);

    TaskResponseDTO getTaskById(Long taskId, int userId);
}
