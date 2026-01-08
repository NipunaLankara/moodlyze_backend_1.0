package com.example.user_service.service.impl;

import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.entity.User;
import com.example.user_service.exception.AlreadyExistsException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repo.UserRepo;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean saveNewUser(UserSaveDTO userSaveDTO) {

        try {
            if (userRepo.existsByEmail(userSaveDTO.getEmail())) {
                throw new AlreadyExistsException("User already exists");
            }
            User user = userMapper.dtoToEntity(userSaveDTO);
            userRepo.save(user);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Internal server error",e);
        }
    }

    @Override
    public String getRole(String email) {

        try {
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found with email: " + email);
            }
            return user.getUserRole();

        } catch (Exception e){
            throw new RuntimeException("Internal server error",e);
        }
    }

    @Override
    public int getId(String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found with email: " + email);
            }
            return user.getUserId();

        } catch (Exception e){
            throw new RuntimeException("Internal server error",e);
        }
    }
}
