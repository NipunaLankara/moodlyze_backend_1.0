package com.example.auth_service.service.impl;

import com.example.auth_service.entity.AuthUsers;
import com.example.auth_service.entity.LoginActivity;
import com.example.auth_service.repo.LoginActivityRepo;
import com.example.auth_service.service.LoginActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginActivityServiceIMPL implements LoginActivityService {
    @Autowired
    private LoginActivityRepo loginActivityRepo;

    @Override
    public boolean isNewDevice(AuthUsers authUserId, String ipAddress, String userAgent) {
        List<LoginActivity> recent = loginActivityRepo.findTop5ByAuthUserIdOrderByLoginTimeDesc(authUserId);
        for (LoginActivity login : recent) {
            if (login.getIpAddress().equals(ipAddress) && login.getUserAgent().equals(userAgent)) {
                System.out.println("Already Logged In");
                return false; // device already seen
            }
        }
        return true;
    }

    @Override
    public void saveLoginActivity(AuthUsers authUsers, String ipAddress, String userAgent, boolean notify) {
        LoginActivity activity = new LoginActivity();
        activity.setAuthUserId(authUsers);
        activity.setEmail(authUsers.getUsername());
        activity.setIpAddress(ipAddress);
        activity.setUserAgent(userAgent);
        activity.setNotified(notify);
        loginActivityRepo.save(activity);
    }
}
