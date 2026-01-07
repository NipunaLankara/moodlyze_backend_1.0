package com.example.auth_service.service;

import com.example.auth_service.entity.AuthUsers;

public interface LoginActivityService {
    boolean isNewDevice(AuthUsers authUsers, String ipAddress, String userAgent);

    void saveLoginActivity(AuthUsers authUsers, String ipAddress, String userAgent, boolean isNew);
}
