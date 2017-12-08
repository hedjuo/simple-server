package com.github.hedjuo.server;

import com.github.hedjuo.client.Client;
import com.github.hedjuo.client.exceptions.ActionNotFoundException;
import com.github.hedjuo.client.exceptions.ServiceNotFoundException;
import com.github.hedjuo.common.Response;
import com.github.hedjuo.common.Response.Status;
import com.github.hedjuo.server.dto.User;
import com.github.hedjuo.client.exceptions.ServiceException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class ClientTest {

    int port = 4444;
    @BeforeClass
    public void setUp() throws Exception {
        new Thread(() -> new Server(port, 15)).start();
        Thread.currentThread().sleep(2000);
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

    @Test
    public void testUnknownService() throws InterruptedException {
        try {
            Client c = new Client("localhost", port, 0);
            c.remoteCall("unknown-service", "now", new Object[]{});
        } catch (IOException | ServiceException e) {
            if (!(e instanceof ServiceNotFoundException)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testUnknownAction() throws InterruptedException {
        try {
            Client c = new Client("localhost", port, 0);
            c.remoteCall("date-service", "unknown-action", new Object[]{});
        } catch (IOException | ServiceException e) {
            if (!(e instanceof ActionNotFoundException)) {
                Assert.fail();
            }
        }
    }
}