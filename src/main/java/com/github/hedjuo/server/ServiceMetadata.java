package com.github.hedjuo.server;

import com.github.hedjuo.server.annotations.Action;
import com.github.hedjuo.server.annotations.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceMetadata {
    private final String serviceName;
    private final Map<String, ActionMetadata> actions;

    public ServiceMetadata(Class<?> service) {

        this.serviceName = service.getAnnotation(Service.class).name();

        this.actions = Arrays.stream(service.getMethods())
                .filter(method -> method.isAnnotationPresent(Action.class))
                .map(ActionMetadata::new)
                .collect(Collectors.toMap(ActionMetadata::getActionName, item -> item));
    }

    public String getServiceName() {
        return serviceName;
    }

    public Map<String, ActionMetadata> getActions() {
        return actions;
    }

    public ActionMetadata getAction(String methodName) {
        return actions.get(methodName);
    }
}
