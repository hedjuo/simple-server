package com.github.hedjuo.server;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Request implements Serializable {
    private final String sessionId;
    private final int requestId;
    private final String serviceName;
    private final String actionName;
    private final Object[] parameters;

    public Request(final String sessionId, final int requestId, final String serviceName, final String actionName, final Object[] parameters) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.actionName = actionName;
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

    public String getActionName() {
        return actionName;
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
        sb.append("], actionName=["+ actionName);
        sb.append("], requestId=["+requestId+"], parameters=[");
        sb.append(Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(", ")));
        sb.append("]");
        return sb.toString();
    }
}
