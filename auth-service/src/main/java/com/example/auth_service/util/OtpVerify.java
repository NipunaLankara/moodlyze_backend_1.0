package com.example.auth_service.util;

import com.example.auth_service.exception.InvalidOtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpVerify {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String getKey(String email) {
        return "otp:" + email;
    }

    // Verify OTP and consume (delete)
    public void verifyAndDelete(String email, String otp) {

        String key = getKey(email);

        String savedOtp = redisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new InvalidOtpException("OTP expired or not found.");
        }

        if (!savedOtp.equals(otp)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // OTP correct → delete (one time use)
        redisTemplate.delete(key);
    }
}
