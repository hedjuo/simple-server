package com.github.hedjuo.server;

import com.github.hedjuo.server.guice.ServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    public static Injector INJECTOR = Guice.createInjector(new ServerModule());

    private ConnectionHandler connectionHandler = INJECTOR.getInstance(ConnectionHandler.class);

    public Server(int port) {
        Thread.currentThread().setName("Client Acceptor");

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
