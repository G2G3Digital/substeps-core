package com.technophobia.substeps.model.exception;

//TODO: we should be able to identify these at parse time
public class UnimplementedStepException extends SubstepsRuntimeException {

    private static final long serialVersionUID = 6807673963152251297L;

    public UnimplementedStepException(final String pattern) {
        super(pattern + " is not a recognised step or substep implementation");
    }

}
