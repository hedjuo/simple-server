package com.github.hedjuo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;
    private ConnectionHandler connectionHandler = new ConnectionHandler();

    public Server(int port) {
        Thread.currentThread().setName("Client Acceptor");
        this.port = port;
        try(final ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Server started. Listening port {}", port);
            int i = 0;
            while(true) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    logger.info("{} client connected.", ++i);
                    connectionHandler.registerClient(clientSocket);
                } catch (IOException e) {
                    logger.error("Server error occurred:", e);
                }
            }
        } catch (IOException e) {
            logger.error("Port already in use.");
        }
    }

}
