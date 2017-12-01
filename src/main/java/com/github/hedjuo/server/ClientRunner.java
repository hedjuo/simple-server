package com.github.hedjuo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRunner {
    private static Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    public static void run(int portNumber, int clientCount, long delay, int timeout) {
        final AtomicInteger counter = new AtomicInteger(0);

        new Thread(() -> {
            try {
                Client c = new Client("localhost", portNumber, timeout);
                logger.info("{} client connected.", counter.incrementAndGet());
                c.remoteCall("date-service", "sleep", new Object[]{delay});
            } catch (IOException e) {
                logger.error("Unable to connect to the Server.", e);
            } catch (Throwable t) {
                logger.error("Fail with {} client", counter.get());
            }
        }).start();

        for(int i=0; i < clientCount; i++) {
            new Thread(() -> {
                try {
                    Client c = new Client("localhost", portNumber, timeout);
                    logger.error("{} client connected.", counter.incrementAndGet());
                    c.remoteCall("date-service", "now", new Object[]{});
                } catch (IOException e) {
                    logger.error("Unable to connect to the Server.", e);
                } catch (Throwable t) {
                    logger.error("Fail with {} client", counter.get());
                }
             }).start();
        }
    }
}
