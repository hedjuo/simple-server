package com.github.hedjuo.server;

import com.github.hedjuo.server.exceptions.ValidationException;
import com.github.hedjuo.server.services.metadata.Service;
import com.github.hedjuo.server.services.metadata.ServiceAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class RunServiceActionTask implements Callable<Object> {
    private final Service service;
    private final String actionName;
    private final Object[] parameters;

    public RunServiceActionTask(final Service service, final String actionName, final Object[] parameters) {
        this.service = service;
        this.actionName = actionName;
        this.parameters = parameters;
    }

    @Override
    public Object call() throws Exception {
        validate();
        return service.getAction(this.actionName).getMethod().invoke(service.getInstance(), parameters);

    }

    private void validate() throws ValidationException {
        List<String> validationErrors = new ArrayList<>();

        final ServiceAction action = this.service.getAction(actionName);

        if (action.getParametersCount() != this.parameters.length) {
            validationErrors.add(String.format("Parameters count for [%s] action mismatched. Required: %s, passed: %s", action.getActionName(), action.getParametersCount(), this.parameters.length));
            throw new ValidationException(validationErrors);
        }

        List<String> parameterTypeErrors = new ArrayList<>();
        final Class<?>[] parameterTypes = action.getParameterTypes();
        for (int i = 0; i< parameterTypes.length; i++) {
            String declaredParameterTypeName = parameterTypes[i].getName();
            String providedParameterTypeName = this.parameters[i].getClass().getName();
            if (!providedParameterTypeName.equals(declaredParameterTypeName)) {
                parameterTypeErrors.add(String.format("Argument type for [%s] action mismatch. At position %s got '%s' but required '%s'", action.getActionName(), i+1, providedParameterTypeName, declaredParameterTypeName));
            }
        }

        if (!parameterTypeErrors.isEmpty()) {
            validationErrors.add(parameterTypeErrors.stream().collect(Collectors.joining(" ")));
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }
    }
}
