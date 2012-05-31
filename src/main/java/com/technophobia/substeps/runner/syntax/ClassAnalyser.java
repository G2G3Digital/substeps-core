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
import com.technophobia.substeps.model.SubSteps;
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

        if (hasAdditionalStepImplementations(loadedClass)) {
            analyseAdditionalStepImplementations(loadedClass, syntax);
        }
    }


    /**
     * @param loadedClass
     * @param stepImplementationMap
     * @param m
     */
    private void analyseMethod(final Class<?> loadedClass, final Syntax syntax, final Method m) {

        final Step stepAnnotation = m.getAnnotation(SubSteps.Step.class);

        // TODO - handle ignores ?
        if (stepAnnotation != null) {

            final StepImplementation impl = StepImplementation.parse(stepAnnotation.value(),
                    loadedClass, m);
            Assert.assertNotNull("unable to resolve the keyword / method for: " + stepAnnotation.value()
                    + " in class: " + loadedClass, impl);

            syntax.addStepImplementation(impl);
        }
    }


    /**
     * Determines if class defers step implementations to another class
     * 
     * @param loadedClass
     * @return true if it defers, false otherwise
     */
    private boolean hasAdditionalStepImplementations(final Class<?> loadedClass) {
        return loadedClass.isAnnotationPresent(AdditionalStepImplementations.class);
    }


    /**
     * Analyses all deferred step implementation classes of the loading class
     * 
     * @param loadedClass
     * @param syntax
     */
    private void analyseAdditionalStepImplementations(final Class<?> loadedClass,
            final Syntax syntax) {
        final AdditionalStepImplementations annotation = loadedClass
                .getAnnotation(AdditionalStepImplementations.class);
        for (final Class<?> stepImplClass : annotation.value()) {
            analyseClass(stepImplClass, syntax);
        }
    }
}
