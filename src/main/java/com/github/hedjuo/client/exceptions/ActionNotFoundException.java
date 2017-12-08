package com.github.hedjuo.client.exceptions;

public class ActionNotFoundException extends ServiceException {
    public ActionNotFoundException(String serviceName) { super(String.format("%s action not found.", serviceName)); }
}
