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
package com.technophobia.substeps.runner.syntax;

import java.lang.reflect.Method;

import org.junit.Assert;

import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.SubSteps.AdditionalStepImplementations;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.Syntax;

public class ClassAnalyser {

    public void analyseClass(final Class<?> loadedClass, final Syntax syntax) {

        final Method[] methods = loadedClass.getDeclaredMethods();
        if (methods != null) {
            for (final Method m : methods) {
                analyseMethod(loadedClass, syntax, m);
            }
        }

        if (hasAdditionalStepsAnnotation(loadedClass)) {
            analyseAdditionalStepImplementations(loadedClass, syntax, getAdditionalStepClasses(loadedClass));
        }
    }


    /**
     * @param loadedClass
     * @param stepImplementationMap
     * @param m
     * @param syntaxErrorReporter
     */
    private void analyseMethod(final Class<?> loadedClass, final Syntax syntax, final Method m) {

        // TODO - handle ignores ?
        if (isStepMethod(m)) {
            final String stepValue = stepValueFrom(m);

            final StepImplementation impl = StepImplementation.parse(stepValue, loadedClass, m);
            Assert.assertNotNull("unable to resolve the keyword / method for: " + stepValue + " in class: "
                    + loadedClass, impl);

            syntax.addStepImplementation(impl);
        }
    }


    /**
     * Determines if class defers step implementations to another class
     * 
     * @param loadedClass
     * @return true if it defers, false otherwise
     */
    protected boolean hasAdditionalStepsAnnotation(final Class<?> loadedClass) {
        return loadedClass.isAnnotationPresent(AdditionalStepImplementations.class);
    }


    /**
     * Returns the Additional Step implementations this class defers to
     * 
     * @param loadedClass
     *            The class containing the annotation
     * @return The classes this class defers steps to
     */
    protected Class<?>[] getAdditionalStepClasses(final Class<?> loadedClass) {
        return loadedClass.getAnnotation(AdditionalStepImplementations.class).value();
    }


    /**
     * Determines if this method is a step definition method
     * 
     * @param m
     *            method
     * @return true if it is a step, otherwise false
     */
    protected boolean isStepMethod(final Method m) {
        return m.isAnnotationPresent(Step.class);
    }


    /**
     * Returns the step value for this method
     * 
     * @param m
     *            The method containing the step definition
     * @return The value associated with the step definition
     */
    protected String stepValueFrom(final Method m) {
        return m.getAnnotation(Step.class).value();
    }


    /**
     * Analyses all deferred step implementation classes of the loading class
     * 
     * @param loadedClass
     * @param syntax
     * @param syntaxErrorReporter
     */
    private void analyseAdditionalStepImplementations(final Class<?> loadedClass, final Syntax syntax,
            final Class<?>[] additionalStepImplementationClasses) {
        for (final Class<?> stepImplClass : additionalStepImplementationClasses) {
            analyseClass(stepImplClass, syntax);
        }
    }
}
