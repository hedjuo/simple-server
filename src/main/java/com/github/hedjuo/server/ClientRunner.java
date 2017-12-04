package com.github.hedjuo.server;

import com.github.hedjuo.server.dto.User;
import com.github.hedjuo.server.exceptions.client.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRunner {
    private static Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    public static void run(int portNumber, int clientCount, long delay, int timeout) {
        final AtomicInteger counter = new AtomicInteger(0);
        for(int i=0; i < clientCount; i++) {
            new Thread(() -> {
                int clientNumber = counter.incrementAndGet();
                try {
                    Client c = new Client("localhost", portNumber, timeout);
                    logger.info("{} client connected.", clientNumber);
                    c.auth(new User("user"));
                    c.remoteCall("date-service", "sleep", new Object[]{delay});
                    c.remoteCall("date-service", "now", new Object[]{});
                    c.disconnect();
                } catch (IOException e) {
                    logger.error("Unable to connect to the Server.", e);
                } catch (ServiceException e) {
                    logger.error("Fail with {} client. Exception: [{}]", clientNumber, e.getMessage());
                }
             }).start();
        }
    }
}
