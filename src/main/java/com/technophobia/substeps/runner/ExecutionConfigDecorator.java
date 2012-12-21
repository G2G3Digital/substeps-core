/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
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
