package com.github.hedjuo.server.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {
    private final List<String> validationErrors = new ArrayList<>();
    public ValidationException(List<String> errors) {
        this.validationErrors.addAll(errors);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
