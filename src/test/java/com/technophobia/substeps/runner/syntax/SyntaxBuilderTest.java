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

import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.model.exception.DuplicatePatternException;
import com.technophobia.substeps.model.exception.DuplicateStepImplementationException;
import com.technophobia.substeps.stepimplementations.DuplicateStepImplementations;
import com.technophobia.substeps.stepimplementations.MockStepImplementations;
import com.technophobia.substeps.stepimplementations.MockStepImplementationsContainer;
import com.technophobia.substeps.steps.TestStepImplementations;

/**
 * 
 * 
 * @author imoore
 * 
 */
public class SyntaxBuilderTest {

    @Test(expected = DuplicatePatternException.class)
    public void testDuplicateDefinitionsThrowError() {
        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        final List<Class<?>> stepImpls = new ArrayList<Class<?>>();
        stepImpls.add(MockStepImplementations.class);

        SyntaxBuilder.buildSyntax(stepImpls, new File("./target/test-classes/substeps/duplicates.substeps"));
    }

    @Test(expected = DuplicateStepImplementationException.class)
    public void testDuplicateStepImplementaionsThrowError() {
        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        final List<Class<?>> stepImpls = new ArrayList<Class<?>>();
        stepImpls.add(DuplicateStepImplementations.class);

        SyntaxBuilder.buildSyntax(stepImpls, new File("./target/test-classes/substeps/simple.substeps"));
    }

    @Test
    public void testSyntaxBuilder() {

        final List<Class<?>> stepImpls = new ArrayList<Class<?>>();
        stepImpls.add(MockStepImplementations.class);

        checkSyntaxBuilderWithStepImpls(stepImpls);
    }

    @Test
    public void testSyntaxBuilderWithDeferringStepImplementations() {

        final List<Class<?>> stepImpls = new ArrayList<Class<?>>();
        stepImpls.add(MockStepImplementationsContainer.class);

        checkSyntaxBuilderWithStepImpls(stepImpls);
    }

    private void checkSyntaxBuilderWithStepImpls(final List<Class<?>> stepImpls) {
        final Syntax syntax = SyntaxBuilder.buildSyntax(stepImpls, new File(
                "./target/test-classes/substeps/allFeatures.substeps"));

        final List<StepImplementation> stepImplementations = syntax.getStepImplementations();

        Assert.assertFalse("expecting some step impls", stepImplementations.isEmpty());

        System.out.println("\nStep implementations\n");

        for (final StepImplementation impl : stepImplementations) {
            System.out.println(impl.getKeyword() + " " + impl.getValue() + " : "
                    + impl.getImplementedIn().getSimpleName() + "." + impl.getMethod().getName());
        }

        final List<ParentStep> sortedList = syntax.getSortedRootSubSteps();

        System.out.println("\n\n\nSubSteps\n");

        for (final ParentStep parent : sortedList) {
            final StringBuilder buf = new StringBuilder();
            buf.append("Parent: " + parent.getParent().getLine() + " in: " + parent.getSubStepFile());

            for (final Step substep : parent.getSteps()) {
                buf.append("\n\t").append(substep.getLine());
            }
            System.out.println(buf.toString());
        }
    }

    @Test
    public void testSingleWordSubStepDefinition() {

        final Step parent = new Step("ASingleWord", true, new File("./target/test-classes/substeps/bugs.substeps"), 23,
                11);

        Assert.assertNotNull(parent.getPattern());

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(MockStepImplementations.class);

        // final List<Class<?>> stepImpls = new ArrayList<Class<?>>();
        // stepImpls.add(DuplicateStepImplementations.class);

        final Syntax syntax = SyntaxBuilder.buildSyntax(stepImplsList, new File(
                "./target/test-classes/substeps/bugs.substeps"));

        final List<ParentStep> substeps = syntax.getSubStepsMap().get("AnotherSingleWord");

        Assert.assertNotNull(substeps);

        Assert.assertThat(substeps.size(), is(1));
    }
}
