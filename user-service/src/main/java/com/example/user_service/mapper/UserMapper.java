package com.example.user_service.mapper;

import com.example.user_service.dto.request.UserSaveDTO;
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
}
