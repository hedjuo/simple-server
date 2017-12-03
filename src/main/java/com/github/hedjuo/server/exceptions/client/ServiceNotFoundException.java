package com.github.hedjuo.server.exceptions.client;

public class ServiceNotFoundException extends ServiceException {
    public ServiceNotFoundException(String serviceName) { super(String.format("%s service not found.", serviceName)); }
}
