package com.example.email_service.service;

import com.example.email_service.dto.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest request);
}
