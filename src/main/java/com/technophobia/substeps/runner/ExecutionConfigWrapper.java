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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;

import com.technophobia.substeps.model.SubSteps.StepImplementations;

/**
 * Wraps an ExecutionConfig providing extra functionality for core
 * 
 * @author rbarefield
 */
public class ExecutionConfigWrapper extends ExecutionConfigDecorator {

    private static final long serialVersionUID = -6096151962497826502L;

    public ExecutionConfigWrapper(SubstepsExecutionConfig executionConfig) {

        super(executionConfig);
    }

    public void initProperties() {

        if (getStepImplementationClasses() == null) {
            setStepImplementationClasses(getClassesFromConfig(getStepImplementationClassNames()));
        }

        if (getSystemProperties() != null) {

            // TODO - don't want to serialise the logger - read resolve ?

            // log.debug
            System.out.println("Configuring system properties [" + getSystemProperties().size() + "] for execution");
            final Properties existing = System.getProperties();
            getSystemProperties().putAll(existing);
            System.setProperties(getSystemProperties());
        }

        determineInitialisationClasses();

        // log.debug
        System.out.println(printParameters());
    }

    private List<Class<?>> getClassesFromConfig(final String[] config) {
        List<Class<?>> stepImplementationClassList = null;
        for (final String className : config) {
            if (stepImplementationClassList == null) {
                stepImplementationClassList = new ArrayList<Class<?>>();
            }
            Class<?> implClass;
            try {
                implClass = Class.forName(className);
                stepImplementationClassList.add(implClass);

            } catch (final ClassNotFoundException e) {
                Assert.fail("ClassNotFoundException: " + e.getMessage());
            }
        }
        return stepImplementationClassList;
    }

    private String printParameters() {
        return "ExecutionConfig [description=" + getDescription() + ", tags=" + getTags() + ", nonFatalTags="
                + getNonFatalTags() + ", featureFile=" + getFeatureFile() + ", subStepsFileName="
                + getSubStepsFileName() + ", strict=" + isStrict() + ", fastFailParseErrors=" + isFastFailParseErrors()
                + ", nonStrictKeywordPrecedence=" + Arrays.toString(getNonStrictKeywordPrecedence())
                + ", stepImplementationClassNames=" + Arrays.toString(getStepImplementationClassNames())
                + ", initialisationClass=" + Arrays.toString(getInitialisationClass()) + ", stepImplementationClasses="
                + getStepImplementationClasses() + ", initialisationClasses="
                + Arrays.toString(getInitialisationClasses()) + "]";
    }

    public Class<?>[] determineInitialisationClasses() {

        List<Class<?>> initialisationClassList = null;
        if (getStepImplementationClasses() != null) {

            initialisationClassList = new ArrayList<Class<?>>();

            InitialisationClassSorter orderer = new InitialisationClassSorter();

            for (final Class<?> c : getStepImplementationClasses()) {

                final StepImplementations annotation = c.getAnnotation(StepImplementations.class);

                if (annotation != null) {
                    final Class<?>[] initClasses = annotation.requiredInitialisationClasses();

                    if (initClasses != null) {

                        orderer.addOrderedInitialisationClasses(initClasses);
                    }
                }
            }

            initialisationClassList = orderer.getOrderedList();
        }
        if (initialisationClassList == null && getInitialisationClass() != null) {
            initialisationClassList = getClassesFromConfig(getInitialisationClass());
        }

        if (initialisationClassList != null) {
            setInitialisationClasses(initialisationClassList.toArray(new Class<?>[] {}));
        }

        return getInitialisationClasses();
    }
}