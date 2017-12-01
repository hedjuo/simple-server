package com.github.hedjuo.server;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Request implements Serializable {
    private final String sessionId;
    private final int requestId;
    private final String serviceName;
    private final String methodName;
    private final Object[] parameters;

    public Request(final String sessionId, final int requestId, final String serviceName, final String methodName, final Object[] parameters) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Request {sessionId=["+sessionId);
        sb.append("], requestId=["+requestId);
        sb.append("], serviceName=["+serviceName);
        sb.append("], methodName=["+methodName);
        sb.append("], requestId=["+requestId+"], parameters=[");
        sb.append(Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(", ")));
        sb.append("]");
        return sb.toString();
    }
}
