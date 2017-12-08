package com.github.hedjuo.server.task;

import com.github.hedjuo.common.Request;
import com.github.hedjuo.common.Response;
import com.github.hedjuo.common.Response.Status;
import com.github.hedjuo.server.Server;
import com.github.hedjuo.server.ServerConfiguration;
import com.github.hedjuo.server.exceptions.ValidationException;
import com.github.hedjuo.server.services.loader.ServiceLoader;
import com.github.hedjuo.server.services.metadata.Service;
import com.github.hedjuo.server.services.runner.ServiceRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.github.hedjuo.common.Response.Status.*;

public class ClientHandlerTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ClientHandlerTask.class);
    private final Map<String, Service> services = Server.INJECTOR.getInstance(ServiceLoader.class).getServices();
    private final Socket socket;

    private final ServiceRunner serviceRunner = Server.INJECTOR.getInstance(ServiceRunner.class);
    private static final ServerConfiguration configuration = Server.INJECTOR.getInstance(ServerConfiguration.class);
    private static final Semaphore SEMAPHORE = new Semaphore(configuration.getMaxConcurrentExecutionThreads());

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
                    SEMAPHORE.acquire();
                    Future<Request> requestFuture = serviceRunner.execute(() -> (Request) incomingStream.readObject());
                    SEMAPHORE.release();
                    Request request = null;
                    try {
                        request = requestFuture.get(configuration.getClientTimeout(), TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        logger.error("Error: {}", e.getMessage());
                    }  catch (ExecutionException e) {
                        logger.error("Unable to parse request object.");
                    } catch (TimeoutException e) {
                        logger.info("Client response timeout exceed. Disconnect.");
                        sendConnectionCloseResponse(outgoingStream, request);
                        socket.close();
                        return;
                    }
                    if ("disconnect".equals(request.getServiceName())) {
                        sendConnectionCloseResponse(outgoingStream, request);
                        socket.close();
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
                    SEMAPHORE.acquire();
                    final Future<Object> actionResult = serviceRunner.executeService(task);
                    SEMAPHORE.release();

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
                } catch (InterruptedException ignore) {
                }
            }
        } catch (IOException e) {
            logger.error("Unable to send response due to: {}", e.getMessage());
        }
    }

    private void sendConnectionCloseResponse(final ObjectOutputStream outgoingStream, final Request request) throws IOException {
        String sessionId = request != null ? request.getSessionId() : null;
        int requestId = request != null ? request.getRequestId() : -1;

        outgoingStream.writeObject(
                new Response(
                        sessionId,
                        requestId,
                        CONNECTION_CLOSED,
                        null,
                        null));
        logger.info("Client [{}] disconnected.", sessionId);
    }
}
