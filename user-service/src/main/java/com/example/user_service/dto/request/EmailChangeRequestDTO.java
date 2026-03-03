package com.example.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailChangeRequestDTO {
    private String oldEmail;
    private String newEmail;
}
