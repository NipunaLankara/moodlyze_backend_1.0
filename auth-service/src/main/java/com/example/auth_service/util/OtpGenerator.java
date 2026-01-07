package com.example.auth_service.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpGenerator {
    public String generateOtp() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}
