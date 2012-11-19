package com.technophobia.substeps.model.exception;

public class UndefinedStepImplementationException extends SubstepsRuntimeException {

    private static final long serialVersionUID = 6807673963152251297L;

    public UndefinedStepImplementationException(final String pattern) {
        super(pattern + " is not a recognised step or substep implementation");
    }

}
