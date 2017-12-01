package com.github.hedjuo.server;

import com.github.hedjuo.server.annotations.Action;
import com.github.hedjuo.server.annotations.Service;
import com.github.hedjuo.server.exceptions.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServiceManager {
    private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<String, Object> services = new HashMap<>();
    private final Map<String, ServiceMetadata> servicesMeta = new HashMap<>();

    public void init() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream input = null;
        try {
            try {
                File file = new File(classLoader.getResource("services.properties").getFile());
                input = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                input = classLoader.getResourceAsStream("services.properties");
            }

            Properties prop = new Properties();
            prop.load(input);

            final Enumeration serviceNames = prop.propertyNames();
            while (serviceNames.hasMoreElements()) {
                final String serviceClassName = (String) prop.get(serviceNames.nextElement());
                try {
                    loadService(serviceClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Unable to load {}", serviceClassName);
                } catch (IllegalAccessException | InstantiationException e) {
                    logger.error("Internal server error", e);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Fatal error: configuration file not found. Exit.");
            System.exit(1);

        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public void invokeService(final Request request, final ObjectOutputStream outgoingStream) throws ExecutionException, InterruptedException, ServiceNotFoundException {
        final String serviceName = request.getServiceName();

        if (!services.containsKey(serviceName)) {
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

        final ServiceMetadata serviceMetadata = servicesMeta.get(serviceName);
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
                 result = action.getMethod().invoke(services.get(serviceName), parameters);
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

        if (metadata.parametersCount != request.getParameters().length) {
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

    private void loadService(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader classLoader = getClass().getClassLoader();
        Class clazz = classLoader.loadClass(className);
        ServiceMetadata meta = new ServiceMetadata(clazz);
        services.put(meta.serviceName, clazz.newInstance());
        servicesMeta.put(meta.serviceName, meta);
    }

    private class ServiceMetadata {
        private final String serviceName;
        private final Map<String, ActionMetadata> actions;

        public ServiceMetadata(Class<?> service) {

            this.serviceName = service.getAnnotation(Service.class).name();

            this.actions = Arrays.stream(service.getMethods())
                    .filter(method -> method.isAnnotationPresent(Action.class))
                    .map(ActionMetadata::new)
                    .collect(Collectors.toMap(ActionMetadata::getActionName, item -> item));
        }

        public ActionMetadata getAction(String methodName) {
            return actions.get(methodName);
        }
    }

    private class ActionMetadata {
        private final String actionName;
        private final int parametersCount;
        private final Method method;
        private final Class<?>[] parameterTypes;

        public ActionMetadata(final Method method) {
            this.actionName = method.getAnnotation(Action.class).name();
            this.parametersCount = method.getParameterCount();
            this.method = method;
            this.parameterTypes = method.getParameterTypes();
        }

        public String getActionName() {
            return actionName;
        }

        public int getParametersCount() {
            return parametersCount;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public Method getMethod() {
            return method;
        }
    }
}