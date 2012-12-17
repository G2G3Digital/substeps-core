package com.technophobia.substeps.runner;

import java.util.List;
import java.util.Properties;

/**
 * Allows ExecutionConfigs to be decorated
 * 
 * @author rbarefield
 * 
 */
abstract class ExecutionConfigDecorator extends SubstepsExecutionConfig {

    private static final long serialVersionUID = 1L;

    private final SubstepsExecutionConfig decoratedConfig;

    protected ExecutionConfigDecorator(SubstepsExecutionConfig executionConfig) {
        this.decoratedConfig = executionConfig;
    }

    @Override
    public String getDescription() {
        return decoratedConfig.getDescription();
    }

    @Override
    public String getTags() {
        return decoratedConfig.getTags();
    }

    @Override
    public String getNonFatalTags() {
        return decoratedConfig.getNonFatalTags();
    }

    @Override
    public String getFeatureFile() {
        return decoratedConfig.getFeatureFile();
    }

    @Override
    public String getSubStepsFileName() {
        return decoratedConfig.getSubStepsFileName();
    }

    @Override
    public boolean isStrict() {
        return decoratedConfig.isStrict();
    }

    @Override
    public boolean isFastFailParseErrors() {
        return decoratedConfig.isFastFailParseErrors();
    }

    @Override
    public String[] getNonStrictKeywordPrecedence() {
        return decoratedConfig.getNonStrictKeywordPrecedence();
    }

    @Override
    public String[] getStepImplementationClassNames() {
        return decoratedConfig.getStepImplementationClassNames();
    }

    @Override
    public String[] getInitialisationClass() {
        return decoratedConfig.getInitialisationClass();
    }

    public String[] setInitialisationClass() {
        return decoratedConfig.getInitialisationClass();
    }

    @Override
    public Properties getSystemProperties() {
        return decoratedConfig.getSystemProperties();
    }

    @Override
    public List<Class<?>> getStepImplementationClasses() {
        return decoratedConfig.getStepImplementationClasses();
    }

    @Override
    public void setStepImplementationClasses(List<Class<?>> stepImplementationClasses) {
        decoratedConfig.setStepImplementationClasses(stepImplementationClasses);
    }

    @Override
    public Class<?>[] getInitialisationClasses() {
        return decoratedConfig.getInitialisationClasses();
    }

    @Override
    public void setInitialisationClasses(Class<?>[] initialisationClasses) {
        decoratedConfig.setInitialisationClasses(initialisationClasses);
    }

}
