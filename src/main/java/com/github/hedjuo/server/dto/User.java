package com.github.hedjuo.server.dto;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;

    public User(final String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return String.format("User[login: %s]", login);
    }
}
