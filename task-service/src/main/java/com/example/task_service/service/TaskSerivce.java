package com.example.task_service.service;

import com.example.task_service.dto.request.TaskCreateRequestDTO;

public interface TaskSerivce {
    String createNewTask(TaskCreateRequestDTO taskCreateRequestDTO, int userId);
}
