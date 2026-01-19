package com.example.user_service.dto.request;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String name;
    private String email;     // can be same or new
    private String address;
    private String contactNumber;
}
