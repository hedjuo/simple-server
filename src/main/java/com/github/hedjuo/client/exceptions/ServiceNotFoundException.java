package com.github.hedjuo.client.exceptions;

public class ServiceNotFoundException extends ServiceException {
    public ServiceNotFoundException(String serviceName) { super(String.format("%s service not found.", serviceName)); }
}
