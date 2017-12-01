package com.github.hedjuo.server.services;

import com.github.hedjuo.server.annotations.Action;
import com.github.hedjuo.server.annotations.Service;
import com.github.hedjuo.server.dto.User;

import java.util.UUID;

@Service(name = "auth")
public class AuthService {

    public AuthService() {
    }

    @Action(name = "login")
    public String getCurrentDate(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }
}
