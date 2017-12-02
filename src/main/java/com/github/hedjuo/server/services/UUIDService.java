package com.github.hedjuo.server.services;

import java.util.UUID;

public class UUIDService {
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
