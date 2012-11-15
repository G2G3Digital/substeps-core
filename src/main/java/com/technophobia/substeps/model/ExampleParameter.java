package com.technophobia.substeps.model;

import java.util.Collections;
import java.util.Map;

public class ExampleParameter {

    private final int lineNumber;
    private final Map<String, String> parameters;


    public ExampleParameter(final int lineNumber, final Map<String, String> parameters) {
        this.lineNumber = lineNumber;
        this.parameters = parameters;
    }


    public int getLineNumber() {
        return lineNumber;
    }


    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
}
