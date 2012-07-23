package com.technophobia.substeps.runner.runtime;

public class StepClassLocator extends PredicatedClassLocator {

    public StepClassLocator(final String path) {
        super(new StepClassFilter(), new ClassLoadingFunction(path));
    }
    
    public StepClassLocator(final String path, ClassLoader classLoader){
    	super(new StepClassFilter(), new ClassLoadingFunction(classLoader, path));
    }
}
