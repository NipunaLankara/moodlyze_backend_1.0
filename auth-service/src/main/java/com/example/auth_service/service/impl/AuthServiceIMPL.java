package com.example.auth_service.service.impl;

import com.example.auth_service.dto.request.EmailRequestDTO;
import com.example.auth_service.dto.request.LoginRequestDTO;
import com.example.auth_service.dto.request.OtpVerifyDTO;
import com.example.auth_service.dto.request.UserSaveDTO;
import com.example.auth_service.dto.response.LoginResponseDTO;
import com.example.auth_service.entity.AuthUsers;
import com.example.auth_service.exception.AlreadyExistsException;
import com.example.auth_service.repo.AuthUserRepo;
import com.example.auth_service.service.*;
import com.example.auth_service.util.JwtUtil;
import com.example.auth_service.util.OtpGenerator;
import com.example.auth_service.util.OtpVerify;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceIMPL implements AuthService {
    @Autowired
    private AuthUserRepo authUserRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserApiClient userApiClient;
    @Autowired
    private OtpGenerator otpGenerator;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OtpVerify otpVerify;
    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    @Lazy // MANDATORY: This prevents the circular dependency/StackOverflow
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginActivityService loginActivityService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private EmailApiClient emailApiClient;


    //     SIGN UP — SAVE DATA IN REDIS
    // -----------------------------------
    @Override
    public String saveNewUser(UserSaveDTO userSaveDTO) throws JsonProcessingException {

        String email = userSaveDTO.getEmail();

        // Check email in DB
        if (authUserRepo.existsByUsername(email)) {
            throw new AlreadyExistsException("Email already exists");
        }

        //  Save user  Redis
        String tempKey = "tempUser:" + email;
        String json = objectMapper.writeValueAsString(userSaveDTO); // Convert object to JSON

        stringRedisTemplate.opsForValue().set(tempKey, json, Duration.ofMinutes(10));

        String otp = otpGenerator.generateOtp();
        String otpKey = "otp:" + email;

        stringRedisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(5));

        // Send OTP email
        EmailRequestDTO request = new EmailRequestDTO();
        request.setTo(email);
        request.setSubject("Book Shop Registration OTP");
        request.setBody("Your OTP is: " + otp);

        emailApiClient.sendEmail(request);


        return "OTP sent to email. " + otp;
    }


    //         VERIFY OTP — SAVE USER
    // -----------------------------------
    @Override
    public String verifyOtp(OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException {

        String email = otpVerifyDTO.getEmail();

        // Verify OTP
        otpVerify.verifyAndDelete(email, otpVerifyDTO.getOtp());

        // Retrieve temporary user data from Redis
        String tempKey = "tempUser:" + email;
        String tempJson = stringRedisTemplate.opsForValue().get(tempKey);

        if (tempJson == null) {
            throw new RuntimeException("Signup expired. Please register again.");
        }

        // Convert JSON back to object
        UserSaveDTO userSaveDTO = objectMapper.readValue(tempJson, UserSaveDTO.class);


        //       SAVE USER IN DATABASE
        // -----------------------------------
        AuthUsers authUser = new AuthUsers(
                userSaveDTO.getEmail(),
                passwordEncoder.encode(userSaveDTO.getPassword())
        );

        authUser.setAcvtiveStatus(true);
        authUser.setOptStatus("ACTIVE");

        authUserRepo.save(authUser);


        //   CALL USER-SERVICE AFTER VERIFIED
        // -----------------------------------
        try {
//            Boolean saved = restTemplate.postForObject(
//                    "http://localhost:8082/api/v1/user/save",
//                    userSaveDTO,
//                    Boolean.class
//            );

            boolean saved = userApiClient.save(userSaveDTO);

            if (!saved) {
                throw new RuntimeException("User-service failed to save user details.");
            }

        } catch (Exception e) {
            throw new RuntimeException("User-service unavailable. Try again later.", e);
        }

        // Clean Redis temporary data
        stringRedisTemplate.delete(tempKey);

        return "Account created & verified successfully!";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUsers authUser = authUserRepo.findByUsername(username);
        if (authUser == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        String role = "USER"; // Default role
        try {
            role = userApiClient.getRole(username);
            if (role == null) role = "USER";
        } catch (Exception e) {
            System.err.println("Could not fetch role from user-service: " + e.getMessage());
            // Use default role so login doesn't crash completely
        }

        return new org.springframework.security.core.userdetails.User(
                authUser.getUsername(),
                authUser.getPassword(),
                getAuthority(role)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(String userRole) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.toUpperCase()));
        return authorities;
    }

    @Override
    public Object createJwtTokenAndLogin(LoginRequestDTO dto) throws Exception {
        // Authenticate FIRST (This throws BadCredentialsException if login fails)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserEmail(), dto.getPassword())
        );
        String userEmail = dto.getUserEmail();
        Integer userId= userApiClient.getUserId(userEmail);
        String role = userApiClient.getRole(userEmail);   //  Use UserAPIClient.........

        String generatedToken = jwtUtil.generateToken(userEmail, userId, role);


        AuthUsers authUsers = authUserRepo.findByUsername(userEmail);

        // Save Login Activity........
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        boolean isNew = loginActivityService.isNewDevice(authUsers, ipAddress, userAgent);
        loginActivityService.saveLoginActivity(authUsers, ipAddress, userAgent, isNew);
        if (isNew) {
            EmailRequestDTO request = new EmailRequestDTO();
            request.setTo(authUsers.getUsername());
            request.setSubject("New Login Detected");
            request.setBody(
                    "Hello,\n\n" +
                            "New login detected.\n" +
                            "IP: " + ipAddress + "\n" +
                            "Device: " + userAgent
            );
            emailApiClient.sendEmail(request);

        }
        return new LoginResponseDTO(userEmail,role, generatedToken);
    }
}
