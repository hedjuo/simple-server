package com.github.hedjuo.server;

import com.github.hedjuo.server.exceptions.ServiceNotFoundException;
import com.github.hedjuo.server.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.hedjuo.server.Response.Status.ACTION_NOT_FOUND;
import static com.github.hedjuo.server.Response.Status.SERVICE_NOT_FOUND;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    private final Socket socket;
    private final String session;
    private AtomicInteger requestCounter = new AtomicInteger(0);

    private static Object semaphore = new Object();

    private final ObjectOutputStream outgoingStream;
    private final ObjectInputStream incomingStream;

    public Client(final String host, final int port, int timeout) throws IOException {
        logger.info("Initialize connection to {}:{}.", host, port);
        synchronized (semaphore) {
            this.socket = new Socket(host, port);
            this.socket.setSoTimeout(timeout);
        }
        logger.info("Connected.");

        outgoingStream = new ObjectOutputStream(socket.getOutputStream());
        incomingStream = new ObjectInputStream(socket.getInputStream());

        logger.info("Try to authenticate...");
        final Response response = remoteCall("auth", "login", new Object[]{new User("user")});
        this.session = (String) response.getResult();
        logger.info("Client connected! Session ID: {}", this.session);

    }

    public void disconnect() throws IOException {
        if (!socket.isClosed()) {
            incomingStream.close();
            outgoingStream.close();
            socket.close();
        }
    }

    public Response remoteCall(String serviceName, String methodName, Object[] parameters) throws IOException {
        try {
            Request req = new Request(session, requestCounter.incrementAndGet(), serviceName, methodName, parameters);
            logger.info("Sending request {}", req.toString());

            outgoingStream.writeObject(req);

            logger.info("Sent");

            final Response response = (Response) incomingStream.readObject();
            logger.info("Received: {}", response.toString());
            if (SERVICE_NOT_FOUND.equals(response.getStatus())) {
                throw new ServiceNotFoundException("Service not found");
            }
            if (ACTION_NOT_FOUND.equals(response.getStatus())) {
                throw new ServiceNotFoundException("Method not found");
            }
            return response;
        } catch (ClassNotFoundException e) {
            logger.error("Unable to parse Response object {}");
            return null;
        }
    }
}
