package com.technophobia.substeps.model.exception;

public class SubstepsException extends RuntimeException {

    private static final long serialVersionUID = 4647698987295633906L;

    public SubstepsException() {
        super();
    }

    public SubstepsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SubstepsException(final String message) {
        super(message);
    }

    public SubstepsException(final Throwable cause) {
        super(cause);
    }

}
