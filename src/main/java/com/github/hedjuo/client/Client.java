package com.github.hedjuo.client;

import com.github.hedjuo.client.exceptions.ActionNotFoundException;
import com.github.hedjuo.client.exceptions.ExecutionFailedException;
import com.github.hedjuo.client.exceptions.ServiceException;
import com.github.hedjuo.client.exceptions.ServiceNotFoundException;
import com.github.hedjuo.server.App;
import com.github.hedjuo.server.Request;
import com.github.hedjuo.server.Response;
import com.github.hedjuo.server.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final Socket socket;
    private String session;
    private AtomicInteger requestCounter = new AtomicInteger(0);

    private static Object semaphore = new Object();

    private final ObjectOutputStream outgoingStream;
    private final ObjectInputStream incomingStream;

    public Client(final String host, final int port, int timeout) throws IOException, ServiceException {
        logger.info("Initialize connection to {}:{}.", host, port);
        synchronized (semaphore) {
            this.socket = new Socket(host, port);
            this.socket.setSoTimeout(timeout);
        }
        logger.info("Connected.");

        outgoingStream = new ObjectOutputStream(socket.getOutputStream());
        incomingStream = new ObjectInputStream(socket.getInputStream());
    }
    public void auth(final User user) throws ServiceException {
        logger.info("Try to authenticate...");
        final Response response;
        try {
            response = remoteCall("auth", "login", new Object[]{user});
        } catch (IOException e) {
            throw new ServiceException(String.format("Unable to auth on the server due to: %s", e.getMessage()));
        }
        this.session = (String) response.getResult();
        logger.info("Client connected! Session ID: {}", this.session);
    }

    public void disconnect() throws IOException, ServiceException {
        if (!socket.isClosed()) {
            try {
                remoteCall("disconnect", "", new Object[]{});
                logger.info("Disconnected.");
            } finally {
                incomingStream.close();
                outgoingStream.close();
                socket.close();
            }
        }
    }

    public Response remoteCall(String serviceName, String actionName, Object[] parameters) throws IOException, ServiceException {
        try {
            Request req = new Request(session, requestCounter.incrementAndGet(), serviceName, actionName, parameters);
            logger.info("Sending request {}", req.toString());

            outgoingStream.writeObject(req);

            logger.info("Sent");

            final Response response = (Response) incomingStream.readObject();
            logger.info("Received: {}", response.toString());
            switch (response.getStatus()) {
                case SERVICE_NOT_FOUND :
                    throw new ServiceNotFoundException(serviceName);
                case ACTION_NOT_FOUND:
                    throw new ActionNotFoundException(actionName);
                case ERROR:
                    throw new ExecutionFailedException(serviceName, response.getErrorMessage());
            }
            return response;
        } catch (ClassNotFoundException e) {
            logger.error("Unable to parse Response object {}");
            return null;
        } catch (IOException e) {
            if (e instanceof SocketException && e.getMessage().toLowerCase().contains("broken pipe")) {
                throw new ServiceException("Connection closed");
            }
            throw e;
        }
    }
}
