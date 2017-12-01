package com.github.hedjuo.server.exceptions;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String serviceName) { super(String.format("%s service not found.", serviceName)); }
}
