package com.github.hedjuo.server;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            printHelp();
            System.exit(1);
        }

        switch(args[0]) {
            case "client":
                if(args.length < 3) {
                    System.out.println("Client count parameter is missing.");
                    return;
                }

                int socketTimeout = args.length == 5 ? Integer.valueOf(args[4]) : 0;
                ClientRunner.run(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Long.valueOf(args[3]), socketTimeout);
                break;
            case "server":
                ServerRunner.run(Integer.valueOf(args[1]));
                break;
            default:
                System.out.println("Invalid application mode:\n");
        }
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("Application works in two modes: Client and Serve");
        System.out.println("  Run application with following command: java -jar simple-server-0.1.jar [client|server] [port number]");
    }
}
