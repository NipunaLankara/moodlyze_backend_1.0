package com.example.task_service.service.impl;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.dto.request.TaskUpdateRequestDTO;
import com.example.task_service.dto.response.TaskResponseDTO;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.enums.TaskStatus;
import com.example.task_service.exception.NotFoundException;
import com.example.task_service.exception.TaskServiceException;
import com.example.task_service.mapper.TaskMapper;
import com.example.task_service.repo.TaskRepo;
import com.example.task_service.service.TaskSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

            return taskList.stream()
                    .map(taskMapper::entityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new TaskServiceException("Failed to get all tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public TaskResponseDTO getTaskById(Long taskId, int userId) {
        Task task = taskRepo.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Task with id " + taskId + " not found for this user"));

        return taskMapper.entityToDto(task);
    }


    @Override
    public List<TaskResponseDTO> getAllTasksByStatus(TaskStatus status, int userId) {
        List<Task> taskList = taskRepo.findByStatusAndUserId(status, userId);

        if (taskList.isEmpty()) {
            throw new NotFoundException("No tasks found for status: " + status);
        }

        return taskList.stream()
                .map(taskMapper::entityToDto)
                .collect(Collectors.toList());
    }

    // GET TODAY TASKS BY STATUS
    @Override
    public List<TaskResponseDTO> getTodayTasksByStatus(
            TaskStatus status,
            int userId
    ) {

        LocalDate today = LocalDate.now();

        List<Task> tasks =
                taskRepo.findByUserIdAndStatusAndTaskDate(
                        userId,
                        status,
                        today
                );

        if (tasks.isEmpty()) {
            throw new NotFoundException(
                    "No tasks found for today with status: " + status
            );
        }

        return tasks.stream()
                .map(taskMapper::entityToDto)
                .toList();
    }

    @Override
    public List<TaskResponseDTO> getOverdueTasks(int userId) {

        LocalDate today = LocalDate.now();

        List<Task> tasks =
                taskRepo.findByUserIdAndStatusAndTaskDateBefore(
                        userId,
                        TaskStatus.PENDING,
                        today
                );

        if (tasks.isEmpty()) {
            throw new NotFoundException("No overdue tasks found");
        }

        return tasks.stream()
                .map(taskMapper::entityToDto)
                .toList();
    }

    @Override
    public void markTaskCompleted(Long taskId) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        task.setStatus(TaskStatus.COMPLETED);
        taskRepo.save(task);
    }

    @Override
    public TaskResponseDTO updateTask(Long taskId, int userId, TaskUpdateRequestDTO dto) {

        Task task = taskRepo.findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Task with id " + taskId + " not found for this user")
                );

        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        if (dto.getEstimatedTimeMinutes() != null) {
            task.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        }
        if (dto.getDeadlineTime() != null) {
            task.setDeadlineTime(dto.getDeadlineTime());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        if (dto.getTaskDate() != null) {
            task.setTaskDate(dto.getTaskDate());
        }

        Task updatedTask = taskRepo.save(task);

        return taskMapper.entityToDto(updatedTask);
    }

    @Override
    public String delete(Long taskId, int userId) {

        if (taskRepo.findByIdAndUserId(taskId, userId).isPresent()) {
            taskRepo.deleteById(taskId);
            return "Task deleted successfully";
        } else {
            throw new NotFoundException("Task with id " + taskId + " not found for this user");
        }
    }



}
