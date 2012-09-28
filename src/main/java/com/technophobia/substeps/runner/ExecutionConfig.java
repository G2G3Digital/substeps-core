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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.SubStepConfigurationException;
import com.technophobia.substeps.model.SubSteps.StepImplementations;

/**
 * @author ian
 */
public class ExecutionConfig {

    private final Logger log = LoggerFactory.getLogger(ExecutionConfig.class);

    /**
     * 
     * @parameter
     */
    private String description;

    /**
     * @parameter
     */
    private String tags;

    /**
     * @parameter
     */
    private String nonFatalTags;

    /**
     * @parameter
     * @required
     */
    private String featureFile;
    /**
     * @parameter
     * @required
     */
    private String subStepsFileName;
    /**
     * 
     * @parameter default-value=true
     * @required
     */
    private boolean strict = true;

    /**
     * 
     * @parameter default-value=true
     * @required
     */
    private boolean fastFailParseErrors = true;

    /**
     * @parameter
     */
    private Properties systemProperties;

    /**
     * @parameter
     */
    private String[] nonStrictKeywordPrecedence;
    /**
     * @parameter
     * @required
     */
    private String[] stepImplementationClassNames;
    /**
     * @parameter
     */
    private String[] initialisationClass;
    /**
	 * 
	 */
    private List<Class<?>> stepImplementationClasses;
    /**
	 * 
	 */
    private Class<?>[] initialisationClasses;


    public String getNonFatalTags() {
        return nonFatalTags;
    }


    public void setNonFatalTags(final String nonFatalTags) {
        this.nonFatalTags = nonFatalTags;
    }


    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }


    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(final String tags) {
        this.tags = tags;
    }


    /**
     * @return the featureFile
     */
    public String getFeatureFile() {
        return featureFile;
    }


    /**
     * @param featureFile
     *            the featureFile to set
     */
    public void setFeatureFile(final String featureFile) {
        this.featureFile = featureFile;
    }


    /**
     * @return the subStepsFileName
     */
    public String getSubStepsFileName() {
        return subStepsFileName;
    }


    /**
     * @param subStepsFileName
     *            the subStepsFileName to set
     */
    public void setSubStepsFileName(final String subStepsFileName) {
        this.subStepsFileName = subStepsFileName;
    }


    /**
     * @return the strict
     */
    public boolean isStrict() {
        return strict;
    }


    /**
     * @param strict
     *            the strict to set
     */
    public void setStrict(final boolean strict) {
        this.strict = strict;
    }


    /**
     * @return the nonStrictKeywordPrecedence
     */
    public String[] getNonStrictKeywordPrecedence() {
        return nonStrictKeywordPrecedence;
    }


    /**
     * @param nonStrictKeywordPrecedence
     *            the nonStrictKeywordPrecedence to set
     */
    public void setNonStrictKeywordPrecedence(
            final String[] nonStrictKeywordPrecedence) {
        this.nonStrictKeywordPrecedence = nonStrictKeywordPrecedence;
    }


    /**
     * @return the stepImplementationClassNames
     */
    public String[] getStepImplementationClassNames() {
        return stepImplementationClassNames;
    }


    /**
     * @param stepImplementationClassNames
     *            the stepImplementationClassNames to set
     */
    public void setStepImplementationClassNames(
            final String[] stepImplementationClassNames) {
        this.stepImplementationClassNames = stepImplementationClassNames;
    }


    /**
     * @return the initialisationClass
     */
    public String[] getInitialisationClass() {
        return initialisationClass;
    }


    /**
     * @param initialisationClass
     *            the initialisationClass to set
     */
    public void setInitialisationClass(final String[] initialisationClass) {
        this.initialisationClass = initialisationClass;
    }


    /**
     * @return the stepImplementationClasses
     */
    public List<Class<?>> getStepImplementationClasses() {
        return stepImplementationClasses;
    }


    /**
     * @param stepImplementationClasses
     *            the stepImplementationClasses to set
     */
    public void setStepImplementationClasses(
            final List<Class<?>> stepImplementationClasses) {
        this.stepImplementationClasses = stepImplementationClasses;
    }


    /**
     * @return the initialisationClasses
     */
    public Class<?>[] getInitialisationClasses() {
        return initialisationClasses;
    }


    /**
     * @param initialisationClasses
     *            the initialisationClasses to set
     */
    public void setInitialisationClasses(final Class<?>[] initialisationClasses) {
        this.initialisationClasses = initialisationClasses;
    }


    public void initProperties() {

        if (getStepImplementationClasses() == null) {
            stepImplementationClasses = getClassesFromConfig(stepImplementationClassNames);
        }

        if (systemProperties != null) {

            log.debug("Configuring system properties ["
                    + systemProperties.size() + "] for execution");
            final Properties existing = System.getProperties();
            systemProperties.putAll(existing);
            System.setProperties(systemProperties);
        }

        determineInitialisationClasses();

        log.debug(printParameters());
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
        return "ExecutionConfig [description=" + description + ", tags=" + tags
                + ", nonFatalTags=" + nonFatalTags + ", featureFile="
                + featureFile + ", subStepsFileName=" + subStepsFileName
                + ", strict=" + strict + ", fastFailParseErrors="
                + fastFailParseErrors + ", nonStrictKeywordPrecedence="
                + Arrays.toString(nonStrictKeywordPrecedence)
                + ", stepImplementationClassNames="
                + Arrays.toString(stepImplementationClassNames)
                + ", initialisationClass="
                + Arrays.toString(initialisationClass)
                + ", stepImplementationClasses=" + stepImplementationClasses
                + ", initialisationClasses="
                + Arrays.toString(initialisationClasses) + "]";
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }


    /**
     * @return the fastFailParseErrors
     */
    public boolean isFastFailParseErrors() {
        return fastFailParseErrors;
    }


    /**
     * @param fastFailParseErrors
     *            the fastFailParseErrors to set
     */
    public void setFastFailParseErrors(final boolean fastFailParseErrors) {
        this.fastFailParseErrors = fastFailParseErrors;
    }


    /**
     * @return
     */
    public Class<?>[] determineInitialisationClasses() {

        List<Class<?>> initialisationClassList = null;
        if (stepImplementationClasses != null) {

            initialisationClassList = new ArrayList<Class<?>>();

            for (final Class<?> c : stepImplementationClasses) {

                final StepImplementations annotation = c
                        .getAnnotation(StepImplementations.class);
                if (annotation != null) {
                    final Class<?>[] initClasses = annotation
                            .requiredInitialisationClasses();

                    if (initClasses != null) {

                        Class<?> predecessor = null;
                        // for (final Class<?> initClass : initClasses){
                        for (int i = initClasses.length; i > 0; i--) {

                            final Class<?> initClass = initClasses[i - 1];

                            if (predecessor == null) {
                                // can just put this one at the end
                                if (!initialisationClassList
                                        .contains(initClass)) {
                                    initialisationClassList.add(initClass);
                                }
                            } else {

                                // put this class before the predecessor
                                final int predecessorIdx = initialisationClassList
                                        .indexOf(predecessor);

                                // is this class already in ?
                                if (initialisationClassList.contains(initClass)) {

                                    // don't need to add, just need to make sure
                                    // the sequencing is ok
                                    final int thisIdx = initialisationClassList
                                            .indexOf(initClass);

                                    if (thisIdx > predecessorIdx) {

                                        // TODO - build up a message

                                        throw new SubStepConfigurationException(
                                                "Incompatible initialisation sequence");
                                    }
                                } else {
                                    initialisationClassList.add(predecessorIdx,
                                            initClass);
                                }
                            }
                            predecessor = initClass;
                        }
                    }
                }
            }
        }
        if (initialisationClassList == null && initialisationClass != null) {
            initialisationClassList = getClassesFromConfig(initialisationClass);
        }

        if (initialisationClassList != null) {
            initialisationClasses = new Class<?>[initialisationClassList.size()];
            // what do we need to execute the runner

            initialisationClasses = initialisationClassList
                    .toArray(initialisationClasses);
        }

        return initialisationClasses;
    }
}