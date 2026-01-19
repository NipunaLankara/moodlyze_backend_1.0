package com.example.user_service.mapper;

import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.dto.response.UserResponseDTO;
import com.example.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User dtoToEntity (UserSaveDTO userSaveDTO) {
        User user = new User(
                userSaveDTO.getName(),
                userSaveDTO.getEmail(),
                userSaveDTO.getAddress(),
                userSaveDTO.getContactNumber(),
                userSaveDTO.getUserRole()
        );
        return user;
    }

    public UserResponseDTO entityToDto (User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                user.getContactNumber()
        );
        return userResponseDTO;
    }
}
