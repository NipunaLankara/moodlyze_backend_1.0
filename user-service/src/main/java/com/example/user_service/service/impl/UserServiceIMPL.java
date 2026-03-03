package com.example.user_service.service.impl;

import com.example.user_service.dto.request.EmailChangeRequestDTO;
import com.example.user_service.dto.request.UserProfileUpdateDTO;
import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.dto.response.UserResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.exception.AlreadyExistsException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.repo.UserRepo;
import com.example.user_service.service.AuthApiClient;
import com.example.user_service.service.UserService;
import com.example.user_service.utill.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthApiClient authApiClient;

    @Override
    public int saveNewUser(UserSaveDTO userSaveDTO) {

        if (userRepo.existsByEmail(userSaveDTO.getEmail())) {
            throw new AlreadyExistsException("User already exists");
        }

        User user = userMapper.dtoToEntity(userSaveDTO);
        User savedUser = userRepo.save(user);

        return savedUser.getUserId();
    }

    @Override
    public String getRole(String email) {

        try {
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with email: " + email);
            }
            return user.getUserRole();

        } catch (Exception e) {
            throw new RuntimeException("Internal server error", e);
        }
    }

    @Override
    public int getId(String email) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with email: " + email);
            }
            return user.getUserId();

        } catch (Exception e) {
            throw new RuntimeException("Internal server error", e);
        }
    }

    @Override
    public Object updateProfile(int userId, UserProfileUpdateDTO dto) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean emailChanged =
                !user.getEmail().equals(dto.getEmail());

        // Update non-sensitive fields immediately
        user.setName(dto.getName());
        user.setAddress(dto.getAddress());
        user.setContactNumber(dto.getContactNumber());

        userRepo.save(user);

        // Email change in auth-service
        if (emailChanged) {
            EmailChangeRequestDTO emailChangeRequestDTO = new EmailChangeRequestDTO(
                    user.getEmail(),
                    dto.getEmail()
            );
            StandardResponse response = authApiClient.requestEmailChange(userId,emailChangeRequestDTO);

//            System.out.println("Email change response  in authapiclient " + response);

            return response.getData();
        }

        return "Profile updated successfully";
    }

    @Override
    public String updateEmail(int userId, String newEmail) {

        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with user id: " + userId);
        } else {

            try {
                User user = userRepo.findByUserId(userId);
                user.setEmail(newEmail);
                userRepo.save(user);
                return "Email updated successfully";

            } catch (Exception e) {
                throw new RuntimeException("Internal server error", e);
            }

        }
    }

    @Override
    public Object deleteUser(int userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String email = user.getEmail();

        // first delete in auth-service
        StandardResponse response = authApiClient.deleteAuthUser(email);

        if (response.getMassage().equals("Success")) {
            userRepo.delete(user);

            return "User deleted successfully";
        } else {
            throw new RuntimeException("Internal server error");
        }

    }

    @Override
    public UserResponseDTO getUserById(int userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserResponseDTO userResponseDTO = userMapper.entityToDto(user);
        return userResponseDTO;

    }
}


