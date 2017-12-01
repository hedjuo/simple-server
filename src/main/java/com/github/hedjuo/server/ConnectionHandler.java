package com.github.hedjuo.server;

import com.github.hedjuo.server.exceptions.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;

public class ConnectionHandler {
    private static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    private ServiceManager serviceManager = new ServiceManager();

    public ConnectionHandler() {
        try {
            this.serviceManager.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerClient(final Socket client) throws IOException {

        new Thread(() -> {
            Thread.currentThread().setName("Client Thread");
            try (
                    ObjectOutputStream outgoingStream = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream incomingStream = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))
            ) {
                logger.info("Start listening incoming request from client.");
                while (true) {
                    try {
                        final Request request = (Request) incomingStream.readObject();
                        if ("disconnect".equals(request.getMethodName())) {
                            logger.info("Client [{}] disconnected. {}", request.getSessionId());
                            return;
                        }
                        logger.info("Received: {}", request.toString());
                        serviceManager.invokeService(request, outgoingStream);
                    } catch (SocketException e) {
                        client.close();
                        if (e.getMessage().contains("Connection reset")) {
                            logger.info("Client disconnected.");
                            return;
                        } else {
                            throw e;
                        }
                    } catch (EOFException ignore) {
                        // Client haven't sent anything yet.
                    } catch (ServiceNotFoundException e) {
                        logger.error("Fail to process request.", e);
                    } catch (ClassNotFoundException ignore) {
                        logger.error("Unable to parse request object.");
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Unexpected sever error", e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
