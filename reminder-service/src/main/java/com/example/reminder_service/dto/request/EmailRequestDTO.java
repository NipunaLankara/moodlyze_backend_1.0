package com.example.reminder_service.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {

    private String to;
    private String subject;
    private String body;
}