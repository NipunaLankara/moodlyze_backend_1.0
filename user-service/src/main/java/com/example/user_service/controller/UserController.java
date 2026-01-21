package com.example.user_service.controller;

import com.example.user_service.dto.request.UserProfileUpdateDTO;
import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.dto.response.UserResponseDTO;
import com.example.user_service.service.UserService;
import com.example.user_service.utill.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")

public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/save")
    public ResponseEntity<StandardResponse> save(
            @Valid @RequestBody UserSaveDTO dto
    ) {
        int userId = userService.saveNewUser(dto);

        return ResponseEntity.ok(
                new StandardResponse(200, "Success", userId)
        );
    }


    @GetMapping("/get-1")
    public String getMessage(){
        return "user-service";
    }

    @GetMapping("/get-role")
    public String getRole(@RequestParam("email") String email) { // Added @RequestParam
        System.out.println("Email issssssssss=====" + email);
        return userService.getRole(email);
    }

    @GetMapping("get-id")
    public int getId(@RequestParam("email") String email) {
        return userService.getId(email);
    }

    @GetMapping("get-user-by-id")
    public ResponseEntity<StandardResponse> getUserById(
            @RequestHeader("X-User-Id") int userId
    ) {
       UserResponseDTO userResponseDTO = userService.getUserById(userId);

        return ResponseEntity.ok(
                new StandardResponse(200, "Success", userResponseDTO)
        );


    }

    @PutMapping("/update-profile")
    public ResponseEntity<StandardResponse> updateProfile(
            @RequestHeader("X-User-Id") int userId,
            @RequestBody UserProfileUpdateDTO dto
    ) {

        Object msg = userService.updateProfile(userId, dto);
        System.out.println("Response msg = "+ msg);

        return ResponseEntity.ok(
                new StandardResponse(200, "Success", msg)
        );
    }

    // This call in auth-service if user change email....
    @PutMapping("/update-email")
    public ResponseEntity<StandardResponse> updateEmail(
            @RequestParam("userId") int userId,
            @RequestParam("newEmail") String newEmail
    ){
        String msg = userService.updateEmail(userId,newEmail);

        return ResponseEntity.ok(
                new StandardResponse(200, "Success", msg)
        );

    }

    @DeleteMapping("delete")
    public ResponseEntity<StandardResponse> deleteUser(
            @RequestHeader("X-User-Id") int userId
    ) {
       Object msg = userService.deleteUser(userId);

        return ResponseEntity.ok(
                new StandardResponse(200, "Success", msg)
        );
    }




}
