package com.github.hedjuo.server.service;

import com.github.hedjuo.server.annotations.Action;
import com.github.hedjuo.server.annotations.Service;
import com.github.hedjuo.server.dto.User;

import javax.inject.Inject;

@Service(name = "auth")
public class AuthService {

    @Inject
    private UUIDService uuidService;

    @Action(name = "login")
    public String getCurrentDate(User user) {
        return uuidService.generateUUID();
    }
}
