package com.github.hedjuo.server;

import com.github.hedjuo.server.Response.Status;
import com.github.hedjuo.server.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.github.hedjuo.server.Response.Status.*;

public class ClientHandlerTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ClientHandlerTask.class);
    private final Map<String, Service> services = Server.INJECTOR.getInstance(ServiceLoader.class).getServices();
    private final Socket socket;

    private ServiceRunner serviceRunner = Server.INJECTOR.getInstance(ServiceRunner.class);

    // last received req time

    public ClientHandlerTask(final Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Thread");
        try (
                ObjectOutputStream outgoingStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream incomingStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))
        ) {
            logger.info("Start listening incoming request from client.");
            while (true) {
                try {
                    final Request request = (Request) incomingStream.readObject();
                    if ("disconnect".equals(request.getServiceName())) {
                        outgoingStream.writeObject(
                                new Response(
                                        request.getSessionId(),
                                        request.getRequestId(),
                                        CONNECTION_CLOSED,
                                        null,
                                        null));
                        socket.close();
                        logger.info("Client [{}] disconnected.", request.getSessionId());
                        return;
                    }
                    logger.info("Received: {}", request.toString());

                    final Service service = services.get(request.getServiceName());
                    if (service == null) {
                        logger.info("Service [{}] not found.", request.getServiceName());
                        outgoingStream.writeObject(
                                new Response(
                                    request.getSessionId(),
                                    request.getRequestId(),
                                    SERVICE_NOT_FOUND,
                                    String.format("\"%s\" service not found", request.getServiceName()),
                                    null));
                        return;
                    }

                    if (service.getAction(request.getActionName()) == null) {
                        logger.info("Action [{}] not found.", request.getActionName());
                        outgoingStream.writeObject(
                                new Response(
                                        request.getSessionId(),
                                        request.getRequestId(),
                                        ACTION_NOT_FOUND,
                                        String.format("\"%s\" action not found", request.getActionName()),
                                        null));
                        return;
                    }

                    RunServiceActionTask task = new RunServiceActionTask(service, request.getActionName(), request.getParameters());
                    final Future<Object> actionResult = serviceRunner.executeService(task);

                    Object result = null;
                    String errMsg = null;
                    Status status = SUCCESS;
                    try {
                        result = actionResult.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Throwable cause = e.getCause();

                        if (cause instanceof ValidationException) {
                            errMsg = ((ValidationException) cause).getValidationErrors().stream().collect(Collectors.joining(", "));
                        } else {
                            errMsg = String.format("Unable get action result due to: {}", e.getMessage());
                        }
                        logger.error(errMsg);
                        status = ERROR;
                    }

                    outgoingStream.writeObject(
                            new Response(
                                request.getSessionId(),
                                request.getRequestId(),
                                status,
                                errMsg,
                                result));

                } catch (SocketException e) {
                    socket.close();
                    if (e.getMessage().contains("Connection reset")) {
                        logger.info("Client disconnected.");
                        return;
                    } else {
                        throw e;
                    }
                } catch (EOFException ignore) {
                    // Client haven't sent anything yet.
                } catch (ClassNotFoundException ignore) {
                    logger.error("Unable to parse request object.");
                }
            }
        } catch (IOException e) {
            logger.error("Unable to send response due to: {}", e.getMessage());
        }
    }
}
