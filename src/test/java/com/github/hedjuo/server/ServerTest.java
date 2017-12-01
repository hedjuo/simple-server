package com.github.hedjuo.server;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    Server s;

    int port = 4444;
    @BeforeMethod
    public void setUp() throws Exception {
        s = new Server(port);
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testRun() throws Exception {

//        new Thread(() -> {
//            try {
//
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        for(int i = 1; i<= 1; i++) {
//
//            new Thread(() -> {
//                try {
//                    Client client = new Client("localhost", port);
//
//                    System.out.println(String.format("Socket created"));
//
//                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//                        System.out.println(String.format("Reader created"));
//                        boolean readInput = true;
//                        while (readInput) {
//                            String response = in.readLine();
//                            System.out.println(String.format(response));
//                            if (response == null) {
//                                continue;
//                            }
//                            System.out.println(String.format(response));
//                            Assert.assertTrue(response.startsWith("response"));
//                            readInput = false;
//                        }
//
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    OpenConnectionProtocol protocol = new OpenConnectionProtocol();
//
//                    final String sessionId = protocol.requestConnection(socket);
//                    System.out.println(String.format("Session id: [%s]", sessionId));
//                    Assert.assertFalse(Strings.isNullOrEmpty(sessionId));
//                    client.disconnect();
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
    }

}