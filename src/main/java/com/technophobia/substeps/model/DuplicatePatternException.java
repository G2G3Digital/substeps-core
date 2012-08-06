package com.technophobia.substeps.model;

public class DuplicatePatternException extends RuntimeException {

    private static final long serialVersionUID = 2426674698756596968L;


    public DuplicatePatternException() {
        super();
    }


    public DuplicatePatternException(final String message, final Throwable cause) {
        super(message, cause);
    }


    public DuplicatePatternException(final String message) {
        super(message);
    }


    public DuplicatePatternException(final Throwable cause) {
        super(cause);
    }
}
