package com.github.hedjuo.server;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Response implements Serializable {
    private final String sessionId;
    private final int requestId;
    private final Status status;
    private final Object result;

    public enum Status {
        SUCCESS, ERROR, SERVICE_NOT_FOUND, ACTION_NOT_FOUND
    }

    public Response(final String sessionId, final int requestId, final Status status, final Object result) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.status = status;
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public Status getStatus() {
        return status;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Response { sessionId = [ "+sessionId);
        sb.append(" ], requestId = [ "+requestId);
        sb.append(" ], status = [ "+status);
        sb.append(" ], result = [ " + (result != null ? result.toString(): "null"));
        sb.append(" ]}");
        return sb.toString();
    }
}
