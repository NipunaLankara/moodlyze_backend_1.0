package com.example.task_service.controller;

import com.example.task_service.dto.request.TaskCreateRequestDTO;
import com.example.task_service.dto.request.TaskUpdateRequestDTO;
import com.example.task_service.dto.response.TaskResponseDTO;
import com.example.task_service.entity.enums.TaskStatus;
import com.example.task_service.service.TaskSerivce;
import com.example.task_service.utill.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin
public class TaskController {
    @Autowired
    private TaskSerivce taskSerivce;

    @PostMapping("/add-new-task")
    public ResponseEntity<StandardResponse> createNewTask(
            @RequestBody @Valid TaskCreateRequestDTO taskCreateRequestDTO,
            @RequestHeader("X-User-Id") int userId
    ) {
        String msg = taskSerivce.createNewTask(taskCreateRequestDTO,userId);

        return new ResponseEntity<>(
                new StandardResponse(200,"Success",msg)
                ,HttpStatus.OK
        );
    }

    @GetMapping("/get-all")
    public ResponseEntity<StandardResponse> getAllTasks(@RequestHeader("X-User-Id") int userId){

        List<TaskResponseDTO> taskResponseDTOList = taskSerivce.getAllTasks(userId);

        return new ResponseEntity<>(
                new StandardResponse(200,"Task List",taskResponseDTOList)
                ,HttpStatus.OK
        );
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<StandardResponse> getTaskById(
            @PathVariable("id") Long taskId,
            @RequestHeader("X-User-Id") int userId
    ){
        TaskResponseDTO taskResponseDTO = taskSerivce.getTaskById(taskId,userId);

        return new ResponseEntity<>(
                new StandardResponse(200,"Success,Get Task",taskResponseDTO)
                ,HttpStatus.OK
        );
    }

    @GetMapping("/get-all-by-status/{status}")
    public ResponseEntity<StandardResponse> getAllTasksByStatus(
            @PathVariable("status") TaskStatus status,
            @RequestHeader("X-User-Id") int userId
    ){
        List<TaskResponseDTO> taskResponseDTOList = taskSerivce.getAllTasksByStatus(status,userId);

        return new ResponseEntity<>(
                new StandardResponse(200,"Task List Status = "+status,taskResponseDTOList)
                ,HttpStatus.OK
        );

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StandardResponse> updateTask(
            @PathVariable("id") Long taskId,
            @RequestBody TaskUpdateRequestDTO taskUpdateRequestDTO,
            @RequestHeader("X-User-Id") int userId
    ) {
        TaskResponseDTO updatedTask =
                taskSerivce.updateTask(taskId, userId, taskUpdateRequestDTO);

        return new ResponseEntity<>(
                new StandardResponse(200, "Task updated successfully", updatedTask),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteTask(
            @PathVariable("id")Long taskId,
            @RequestHeader("X-User-Id") int userId
    ){

        String msg = taskSerivce.delete(taskId,userId);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );
    }






}
