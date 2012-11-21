package com.technophobia.substeps.model.exception;

import java.lang.reflect.Method;

public class StepImplementationException extends SubstepsException {

    private static final long serialVersionUID = 2361658683635343317L;

    private final Class<?> implementingClass;
    private final Method implementingMethod;

    public StepImplementationException(final Class<?> implementingClass, final Method implementingMethod,
            final String message) {
        super(message);
        this.implementingClass = implementingClass;
        this.implementingMethod = implementingMethod;
    }

    public Class<?> getImplementingClass() {
        return implementingClass;
    }

    public Method getImplementingMethod() {
        return implementingMethod;
    }

}
