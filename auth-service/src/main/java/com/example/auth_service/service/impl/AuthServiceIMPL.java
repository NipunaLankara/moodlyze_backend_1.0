package com.example.auth_service.service.impl;

import com.example.auth_service.dto.request.*;
import com.example.auth_service.dto.response.LoginResponseDTO;
import com.example.auth_service.entity.AuthUsers;
import com.example.auth_service.exception.AlreadyExistsException;
import com.example.auth_service.exception.DownstreamServiceException;
import com.example.auth_service.repo.AuthUserRepo;
import com.example.auth_service.service.*;
import com.example.auth_service.util.JwtUtil;
import com.example.auth_service.util.OtpGenerator;
import com.example.auth_service.util.OtpVerify;
import com.example.auth_service.util.StandardResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.security.authentication.AuthenticationManager;
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

    @Override
    public String verifyOtpSaveUser(OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException {

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

        int userId;

        try {
            StandardResponse response = userApiClient.save(userSaveDTO);

            if (response == null || response.getData() == null) {
                throw new DownstreamServiceException("Invalid response from user-service");
            }

            userId = (int) response.getData();

        } catch (feign.RetryableException ex) {
            // service DOWN / connection refused / timeout
            throw new DownstreamServiceException(
                    "User service is unavailable. Please try again later.",
                    ex
            );

        } catch (feign.FeignException ex) {
            // user-service returned 4xx / 5xx
            throw new DownstreamServiceException(
                    "User service error occurred",
                    ex
            );
        }


        //       SAVE AUTH USER IN DATABASE
        AuthUsers authUser = new AuthUsers();
        authUser.setUsername(userSaveDTO.getEmail());
        authUser.setPassword(passwordEncoder.encode(userSaveDTO.getPassword()));
        authUser.setUserId(userId);
        authUser.setAcvtiveStatus(true);
        authUser.setOptStatus("ACTIVE");

        authUserRepo.save(authUser);

        stringRedisTemplate.delete(tempKey);

        return "Account created & verified successfully";
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

//    @Override
//    public Object createJwtTokenAndLogin(LoginRequestDTO dto) throws Exception {
//        // Authenticate FIRST (This throws BadCredentialsException if login fails)
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(dto.getUserEmail(), dto.getPassword())
//        );
//        String userEmail = dto.getUserEmail();
//        Integer userId = userApiClient.getUserId(userEmail);
//        String role = userApiClient.getRole(userEmail);   //  Use UserAPIClient.........
//
//        String generatedToken = jwtUtil.generateToken(userEmail, userId, role);
//
//
//        AuthUsers authUsers = authUserRepo.findByUsername(userEmail);
//
//        // Save Login Activity........
//        String ipAddress = request.getRemoteAddr();
//        String userAgent = request.getHeader("User-Agent");
//        boolean isNew = loginActivityService.isNewDevice(authUsers, ipAddress, userAgent);
//        loginActivityService.saveLoginActivity(authUsers, ipAddress, userAgent, isNew);
//        if (isNew) {
//            EmailRequestDTO request = new EmailRequestDTO();
//            request.setTo(authUsers.getUsername());
//            request.setSubject("New Login Detected");
//            request.setBody(
//                    "Hello,\n\n" +
//                            "New login detected.\n" +
//                            "IP: " + ipAddress + "\n" +
//                            "Device: " + userAgent
//            );
//            emailApiClient.sendEmail(request);
//
//        }
//        return new LoginResponseDTO(userEmail, role, generatedToken);
//    }

    @Override
    public Object createJwtTokenAndLogin(LoginRequestDTO dto) throws Exception {

        String userEmail = dto.getUserEmail();

        // Authenticate password first
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userEmail, dto.getPassword())
        );

        AuthUsers authUser = authUserRepo.findByUsername(userEmail);

        //CHECK IF 2FA ENABLED
        if (authUser.isTwoFactorEnabled()) {

            String otp = otpGenerator.generateOtp();

            // Save OTP in Redis (5 minutes)
            stringRedisTemplate.opsForValue().set(
                    "2fa:" + userEmail,
                    otp,
                    Duration.ofMinutes(5)
            );

            // Send Email
            EmailRequestDTO mail = new EmailRequestDTO();
            mail.setTo(userEmail);
            mail.setSubject("Your 2FA Verification Code");
            mail.setBody("Your login verification code is: " + otp);

            emailApiClient.sendEmail(mail);

            return "2FA_REQUIRED";
        }

        // If 2FA disabled → normal login
        return generateJwtAndSaveLogin(authUser);
    }

    private LoginResponseDTO generateJwtAndSaveLogin(AuthUsers authUsers) {

        String userEmail = authUsers.getUsername();

        Integer userId = userApiClient.getUserId(userEmail);
        String role = userApiClient.getRole(userEmail);

        String generatedToken = jwtUtil.generateToken(userEmail, userId, role);

        // Save login activity
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        boolean isNew = loginActivityService.isNewDevice(authUsers, ipAddress, userAgent);
        loginActivityService.saveLoginActivity(authUsers, ipAddress, userAgent, isNew);

        if (isNew) {
            EmailRequestDTO request = new EmailRequestDTO();
            request.setTo(userEmail);
            request.setSubject("New Login Detected");
            request.setBody(
                    "New login detected.\nIP: " + ipAddress +
                            "\nDevice: " + userAgent
            );
            emailApiClient.sendEmail(request);
        }

        return new LoginResponseDTO(userEmail, role, generatedToken);
    }

    @Override
    public String requestEmailChange(int userId, EmailChangeRequestDTO emailChangeRequestDTO) {

        String oldEmail = emailChangeRequestDTO.getOldEmail();
        String newEmail = emailChangeRequestDTO.getNewEmail();

        if (oldEmail.equals(newEmail)) {
            throw new RuntimeException("New email cannot be same as old email");
        }

        if (authUserRepo.existsByUsername(newEmail)) {
            throw new AlreadyExistsException("Email already exists");
        }

        AuthUsers user = authUserRepo.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // -------- Generate OTP (reuse existing)
        String otp = otpGenerator.generateOtp();

        // -------- Save OTP in Redis
        stringRedisTemplate.opsForValue().set(
                "otp:" + newEmail,
                otp,
                Duration.ofMinutes(5)
        );

//        // -------- Save pending email change
//        stringRedisTemplate.opsForValue().set(
//                "email-change:" + newEmail,   // Fix --- here when user verify otp insert older email,can not change it because it used - Update auth-service DB
//                newEmail,
//                Duration.ofMinutes(10)
//        );

        // -------- Send OTP email (reuse existing email-service)
        EmailRequestDTO mail = new EmailRequestDTO();
        mail.setTo(newEmail);
        mail.setSubject("Verify New Email");
        mail.setBody("Your OTP is: " + otp);

        emailApiClient.sendEmail(mail);

        return "OTP sent to new email address "+otp;
    }

    @Override
    public String verifyEmailChange(int userId, OtpVerifyDTO dto) {

//        String redisKey = "email-change:" + dto.getEmail();
//        String newEmail = stringRedisTemplate.opsForValue().get(redisKey);

        String newEmail = dto.getEmail();

        if (newEmail == null) {
            throw new RuntimeException("Email change request expired");
        }

        // -------- Verify OTP
        otpVerify.verifyAndDelete(newEmail, dto.getOtp());

        // -------- Update auth-service DB
        AuthUsers user = authUserRepo.findByUserId(userId);  // Fix -- here use older email.........
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setUsername(newEmail);
        authUserRepo.save(user);

        // -------- Sync user-service
        userApiClient.updateEmail(userId,newEmail);

//        // -------- Clean Redis
//        stringRedisTemplate.delete(redisKey);

        return "Email updated successfully";
    }

    @Override
    @Transactional
    public String deleteAuthUser(String email) {

        AuthUsers user = authUserRepo.findByUsername(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        authUserRepo.delete(user);
        return "User deleted successfully";
    }

    @Override
    public String set2fa(int id, boolean status) {
        AuthUsers authUsers = authUserRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        authUsers.setTwoFactorEnabled(status);
        authUserRepo.save(authUsers);

        return status ? "2FA Enabled" : "2FA Disabled";

    }

    @Override
    public Object verify2faAndLogin(OtpVerifyDTO dto) {

        String email = dto.getEmail();

        String key = "2fa:" + email;

        String savedOtp = stringRedisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new RuntimeException("OTP expired.");
        }

        if (!savedOtp.equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Delete OTP (one time use)
        stringRedisTemplate.delete(key);

        AuthUsers authUser = authUserRepo.findByUsername(email);

        return generateJwtAndSaveLogin(authUser);
    }

    @Override
    public String requestPasswordReset(String email) {

        AuthUsers user = authUserRepo.findByUsername(email);

        if (user == null) {
            throw new UsernameNotFoundException("No account found with this email");
        }

        String otp = otpGenerator.generateOtp();

        // Save in Redis (5 min)
        stringRedisTemplate.opsForValue().set(
                "reset:" + email,
                otp,
                Duration.ofMinutes(5)
        );

        // Send email
        EmailRequestDTO mail = new EmailRequestDTO();
        mail.setTo(email);
        mail.setSubject("Password Reset OTP");
        mail.setBody("Your password reset OTP is: " + otp);

        emailApiClient.sendEmail(mail);

        return "Password reset OTP sent to email";
    }

    @Override
    public String verifyAndResetPassword(ResetPasswordDTO dto) {

        String key = "reset:" + dto.getEmail();

        String savedOtp = stringRedisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new RuntimeException("OTP expired or invalid");
        }

        if (!savedOtp.equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Delete OTP (one time use)
        stringRedisTemplate.delete(key);

        AuthUsers user = authUserRepo.findByUsername(dto.getEmail());

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        authUserRepo.save(user);

        return "Password reset successfully";
    }

}
