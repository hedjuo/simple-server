package com.github.hedjuo.client.exceptions;

public class ExecutionFailedException extends ServiceException {
    public ExecutionFailedException(String serviceName, String cause) {
        super(String.format("%s service execution failed. Cause: [%s].", serviceName, cause));
    }
}
