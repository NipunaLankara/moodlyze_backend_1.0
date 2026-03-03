package com.example.user_service.service;


import com.example.user_service.dto.request.UserProfileUpdateDTO;
import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.dto.response.UserResponseDTO;
import jakarta.validation.Valid;

public interface UserService {
    int saveNewUser(@Valid UserSaveDTO userSaveDTO);

    String getRole(String email);

    int getId(String email);

    Object updateProfile(int userId, UserProfileUpdateDTO dto);

    String updateEmail(int userId, String newEmail);

    Object deleteUser(int userId);

    UserResponseDTO getUserById(int userId);
}
