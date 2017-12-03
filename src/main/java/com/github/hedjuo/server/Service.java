package com.github.hedjuo.server;

import com.github.hedjuo.server.annotations.Action;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Service {
    private final String serviceName;
    private final Map<String, ServiceAction> actions;
    private final Object instance;

    public Service(Class<?> serviceClass) {
        this.serviceName = serviceClass.getAnnotation(com.github.hedjuo.server.annotations.Service.class).name();
        this.instance = Server.INJECTOR.getInstance(serviceClass);
        this.actions = Arrays.stream(serviceClass.getMethods())
                .filter(method -> method.isAnnotationPresent(Action.class))
                .map(ServiceAction::new)
                .collect(Collectors.toMap(ServiceAction::getActionName, item -> item));
    }

    public String getName() {
        return serviceName;
    }

    public ServiceAction getAction(String actionName) {
        return actions.get(actionName);
    }

    public Object getInstance() {
        return instance;
    }
}
