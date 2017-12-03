package com.github.hedjuo.server.exceptions.client;

public class ActionNotFoundException extends ServiceException {
    public ActionNotFoundException(String serviceName) { super(String.format("%s action not found.", serviceName)); }
}
