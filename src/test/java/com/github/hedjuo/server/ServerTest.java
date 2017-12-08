package com.github.hedjuo.server;

import com.github.hedjuo.server.Response.Status;
import com.github.hedjuo.server.dto.User;
import com.github.hedjuo.client.exceptions.ServiceException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class ServerTest {

    int port = 4444;
    @BeforeMethod
    public void setUp() throws Exception {
        new Thread(() -> new Server(port, 15)).start();
        Thread.currentThread().sleep(2000);
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testOneClientAuth() {

        try {
            Client c = new Client("localhost", port, 0);
            c.auth(new User("testUser"));
        } catch (IOException | ServiceException e) {
            Assert.fail();
        }
    }

    @Test
    public void testOneClientDateService() throws InterruptedException {
        try {
            Client c = new Client("localhost", port, 0);
            c.auth(new User("testUser"));
            final Response response = c.remoteCall("date-service", "now", new Object[]{});
            Assert.assertTrue(Status.SUCCESS.equals(response.getStatus()));
            final Date date = (Date) response.getResult();
            Assert.assertTrue(date != null);
        } catch (IOException | ServiceException e) {
            Assert.fail();
        }
    }
}