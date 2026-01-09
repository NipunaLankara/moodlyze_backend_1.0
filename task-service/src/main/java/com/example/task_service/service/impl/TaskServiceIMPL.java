package com.example.task_service.service.impl;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.dto.response.TaskResponseDTO;
import com.example.task_service.entity.Task;
import com.example.task_service.exception.TaskServiceException;
import com.example.task_service.mapper.TaskMapper;
import com.example.task_service.repo.TaskRepo;
import com.example.task_service.service.TaskSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceIMPL  implements TaskSerivce {
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public String createNewTask(TaskCreateRequestDTO taskCreateRequestDTO, int userId) {

        try {
            Task task = taskMapper.dtoToEntity(taskCreateRequestDTO, userId);
            taskRepo.save(task);
            return "Task created successfully";
        } catch (Exception e) {
            throw new TaskServiceException("Failed to create task: " + e.getMessage(), e);
        }

    }

    @Override
    public List<TaskResponseDTO> getAllTasks(int userId) {
        try {
            List<Task> taskList = taskRepo.findByUserId(userId);

            // Using streams to map entities to DTOs
            return taskList.stream()
                    .map(taskMapper::entityToShortResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new TaskServiceException("Failed to get all tasks: " + e.getMessage(), e);
        }
    }

}
