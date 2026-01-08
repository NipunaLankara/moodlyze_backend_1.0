package com.example.task_service.mapper;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task dtoToEntity(TaskCreateRequestDTO dto, int userId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        task.setTaskDate(dto.getTaskDate());

        if (dto.getDeadlineTime() != null) {
            task.setDeadlineTime(dto.getDeadlineTime());
        }
        return task;
    }

}
