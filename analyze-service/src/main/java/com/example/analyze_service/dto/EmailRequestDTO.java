package com.example.analyze_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
}