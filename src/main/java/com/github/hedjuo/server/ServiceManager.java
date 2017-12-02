package com.github.hedjuo.server;

import com.github.hedjuo.server.exceptions.ServiceNotFoundException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServiceManager {
    private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private final ExecutorService executor = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            10L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    @Inject
    private ServiceLoader serviceLoader;

    public void invokeService(final Request request, final ObjectOutputStream outgoingStream) throws ExecutionException, InterruptedException, ServiceNotFoundException {
        final String serviceName = request.getServiceName();

        if (!serviceLoader.getServices().containsKey(serviceName)) {
            logger.error("Requested service {} not found ", request.getServiceName());
            throw new ServiceNotFoundException(serviceName);
        }

        executor.submit(() -> {
            outgoingStream.writeObject(doAction(request));
            return null;
        });
    }

    private Response doAction(final Request request) {
        final String serviceName = request.getServiceName();
        final String actionName = request.getMethodName();
        final Object[] parameters = request.getParameters();

        final ServiceMetadata serviceMetadata = serviceLoader.getServices().get(serviceName).getMetadata();
        final ActionMetadata action = serviceMetadata.getAction(actionName);

        if (action == null) {
            logger.error("{} action not found", actionName);
            return new Response(request.getSessionId(), request.getRequestId(), Response.Status.ACTION_NOT_FOUND, null);
        }

        List<String> validationErrors = validate(request, action);

        Object result;
        Response.Status status;
        if (!validationErrors.isEmpty()) {
            String errMsg = String.format("Parameter types are mismatched. %s", validationErrors.stream().collect(Collectors.joining("\n")));
            logger.error(errMsg);
            status = Response.Status.ERROR;
            result = errMsg;
        } else {
            try {
                 result = action.getMethod().invoke(serviceLoader.getServices().get(serviceName).getInstance(), parameters);
                 status = Response.Status.SUCCESS;
                 logger.info("Action [{}_{}] successfully returned: [{}]", serviceName, actionName, result);
            } catch (Exception e) {
                status = Response.Status.ERROR;
                result = e.getMessage();
                logger.error("Action [{}_{}] failed with error: [{}]", serviceName, actionName, e.getMessage());
            }

        }
        return new Response(request.getSessionId(), request.getRequestId(), status, result);
    }

    private List<String> validate(final Request request, final ActionMetadata metadata) {
        List<String> validationErrors = new ArrayList<>();

        if (metadata.getParametersCount() != request.getParameters().length) {
            validationErrors.add(String.format("Parameters count mismatched. Required: %s, passed: %s", metadata.getParametersCount(), request.getParameters().length));
        }

        final Object[] params = request.getParameters();
        final Class<?>[] parameterTypes = metadata.getParameterTypes();
        List<String> parameterTypeErrors = new ArrayList<>();
        for (int i = 0; i<parameterTypes.length; i++) {
            String declaredParameterTypeName = parameterTypes[i].getName();
            String providedParameterTypeName = params[i].getClass().getName();
            if (!providedParameterTypeName.equals(declaredParameterTypeName)) {
                parameterTypeErrors.add(String.format("Argument type mismatch. At position %s but required %s", providedParameterTypeName, declaredParameterTypeName));
            }
        }

        if (!parameterTypeErrors.isEmpty()) {
            validationErrors.add(String.format("Parameters type mismatched. %s", parameterTypeErrors.stream().collect(Collectors.joining(" "))));
        }

        return validationErrors;
    }
}
