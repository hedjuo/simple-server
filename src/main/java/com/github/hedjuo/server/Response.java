package com.github.hedjuo.server;

import java.io.Serializable;

public class Response implements Serializable {
    private final String sessionId;
    private final int requestId;
    private final Status status;
    private final Object result;
    private final String errorMessage;

    public enum Status {
        SUCCESS, ERROR, SERVICE_NOT_FOUND, ACTION_NOT_FOUND, CONNECTION_CLOSED
    }

    public Response(final String sessionId, final int requestId, final Status status, final String errorMessage, final Object result) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.status = status;
        this.errorMessage = errorMessage;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Response { sessionId = [ " + sessionId);
        sb.append(" ], requestId = [ " + requestId);
        sb.append(" ], status = [ " + status);
        sb.append(" ], error = [ " + errorMessage);
        sb.append(" ], result = [ " + (result != null ? result.toString(): "null"));
        sb.append(" ]}");
        return sb.toString();
    }
}
