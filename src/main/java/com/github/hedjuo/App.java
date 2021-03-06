package com.github.hedjuo;

import com.github.hedjuo.client.ClientRunner;
import com.github.hedjuo.server.ServerRunner;

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

                int delay = args.length == 4 ? Integer.valueOf(args[3]) : 1000;
                int socketTimeout = args.length == 5 ? Integer.valueOf(args[4]) : 0;
                ClientRunner.run(Integer.valueOf(args[1]), Integer.valueOf(args[2]), delay, socketTimeout);
                break;
            case "server":
                int maxClientCount = args.length == 3 ? Integer.valueOf(args[2]) : 500;
                ServerRunner.run(Integer.valueOf(args[1]), maxClientCount);
                break;
            default:
                System.out.println("Invalid application mode:\n");
        }
    }

    private static void printHelp() {
        System.out.println("Application works in two modes: Server and Test\n");
        System.out.println(
"Test mode runs specified number of clients and execute them in parallel. Each client does simple service request to server\n" +
"to get current date. Also to test server's multitasking ClientRunner sends  prior all regular requests the special one to \"pause\"\n" +
"the service for specified amount of time.\n");
        System.out.println("Usage:");
        System.out.println("  Run application as Server with following command: java -jar simple-server-0.1.jar server [port number] [max client count]");
        System.out.println("  or");
        System.out.println("  Run application in Test Client mode with following command: java -jar simple-server-0.1.jar client [port number] [client number] [delay]");
    }
}
