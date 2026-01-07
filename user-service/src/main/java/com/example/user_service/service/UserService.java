package com.example.user_service.service;


import com.example.user_service.dto.request.UserSaveDTO;
import jakarta.validation.Valid;

public interface UserService {
    boolean saveNewUser(@Valid UserSaveDTO userSaveDTO);

    String getRole(String email);
}
