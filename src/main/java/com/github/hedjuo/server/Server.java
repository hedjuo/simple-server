package com.github.hedjuo.server;

import com.github.hedjuo.server.guice.ServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    public static Injector INJECTOR = Guice.createInjector(new ServerModule());

    private final ExecutorService clientsPool;

    public Server(int port, int maxClientCount) {
        Thread.currentThread().setName("Client Acceptor");

        clientsPool = new ThreadPoolExecutor(
                maxClientCount/2,
                maxClientCount,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        try(final ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Max connected client value {}", maxClientCount);
            logger.info("Server started. Listening port {}", port);
            final AtomicInteger clientCount = new AtomicInteger(0);
            while(true) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    clientCount.incrementAndGet();
                    if (clientCount.get() > maxClientCount) {
                        clientCount.decrementAndGet();
                        logger.error("Client count limit reached.");
                        clientSocket.close();
                        continue;
                    }
                    final CompletableFuture<Void> future = CompletableFuture.runAsync(new ClientHandlerTask(clientSocket), clientsPool);
                    future.thenAccept((result) -> clientCount.decrementAndGet());

                    logger.info("Client-{} connected.", clientCount.get());
                } catch (IOException e) {
                    logger.error("Server error occurred:", e);
                }
            }
        } catch (IOException e) {
            logger.error("Port already in use.");
        }
    }

}
