package com.github.hedjuo.server.service.metadata;

import com.github.hedjuo.server.annotations.Action;

import java.lang.reflect.Method;

public class ServiceAction {
    private final String actionName;
    private final int parametersCount;
    private final Method method;
    private final Class<?>[] parameterTypes;

    public ServiceAction(final Method method) {
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
