package com.github.hedjuo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRunner {
    private static Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    public static void run(int portNumber, int maxClientCount) {

        try {
            logger.info("Initializing server.");
            new Server(portNumber, maxClientCount);
        } catch (Exception e) {
            logger.error("Unexpected error: "+ e.getMessage());
        }
    }
}
