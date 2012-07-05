package com.technophobia.substeps.runner.runtime;

public class StepClassLocator extends PredicatedClassLocator {

    public StepClassLocator(final String path) {
        super(new StepClassFilter(), new ClassLoadingFunction(path));
    }
}
