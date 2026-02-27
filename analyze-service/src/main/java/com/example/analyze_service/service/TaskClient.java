package com.example.analyze_service.service;

import com.example.analyze_service.util.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "task-service")
public interface TaskClient {

    @GetMapping("/api/v1/tasks/get-all-by-status/{status}")
    ResponseEntity<StandardResponse> getTasksByStatus(
            @PathVariable("status") String status,
            @RequestHeader("X-User-Id") int userId
    );

    @GetMapping("/api/v1/tasks/get-today-by-status/{status}")
    ResponseEntity<StandardResponse> getTodayTasksByStatus(
            @PathVariable("status") String status,
            @RequestHeader("X-User-Id") int userId
    );
}