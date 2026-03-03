package com.example.ai_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDTO {
    private List<String> activities; // list of calming activities
}