package com.example.user_service.controller;

import com.example.user_service.dto.request.UserSaveDTO;
import com.example.user_service.service.UserService;
import com.example.user_service.utill.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin

public class UserController {
    @Autowired
    private UserService userService;

//    @PostMapping("/save")
//    public ResponseEntity<StandardResponse> save(@Valid @RequestBody UserSaveDTO userSaveDTO) {
//        System.out.println(userSaveDTO.getName());
//        String message = userService.saveNewUser(userSaveDTO);
//
//        return new ResponseEntity<StandardResponse>(
//                new StandardResponse(200,"User Registered Successfully",message),
//                HttpStatus.CREATED
//        );
//    }

    @PostMapping("/save")
    public boolean save(@Valid @RequestBody UserSaveDTO userSaveDTO) {
        System.out.println(userSaveDTO.getName());
        boolean message = userService.saveNewUser(userSaveDTO);
        return message;
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
}
