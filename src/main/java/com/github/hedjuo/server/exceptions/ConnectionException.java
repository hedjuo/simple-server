package com.github.hedjuo.server.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(String msg) { super(msg); }
    public ConnectionException(String msg, Throwable reason) { super(msg, reason); }
}
