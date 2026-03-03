package com.example.auth_service.dto.request;

import lombok.Data;

@Data
public class EmailChangeRequestDTO {
    private String oldEmail;
    private String newEmail;
}
