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

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.stepimplementations.MockStepImplementations;
import com.technophobia.substeps.steps.TestStepImplementations;

/**
 * @author imoore
 * 
 */
public class JunitFeatureRunnerTest extends BaseJunitFeatureRunnerTest {

    @Test(expected = SubstepsConfigurationException.class)
    public void testScenarioWithMissingStepsCausesFailure() {
        final String feature = "./target/test-classes/features/scenario_missing_steps.feature";

        final String tag = "@scenario_with_missing_steps";
        final String substeps = "./target/test-classes/substeps/error.substeps";

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        runner.init(this.getClass(), stepImplsList, feature, tag, substeps,
                null);
        runner.run(null);
    }


    @Test(expected = SubstepsConfigurationException.class)
    public void testMissingSubStepCausesFailure() {
        final String feature = "./target/test-classes/features/error.feature";
        final String tag = "@bug_missing_sub_step_impl";
        final String substeps = "./target/test-classes/substeps/error.substeps";

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        runner.init(this.getClass(), stepImplsList, feature, tag, substeps,
                null);
    }


    @Test
    public void testTableParameterPassingWithOutlines() {

        final String feature = "./target/test-classes/features/bugs.feature";
        final String tag = "@table_params_and_outline";
        final String substeps = null;

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        runner.init(this.getClass(), stepImplsList, feature, tag, substeps,
                null);

        final TestStepImplementations stepImpls = new TestStepImplementations();
        final TestStepImplementations spy = spy(stepImpls);

        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(TestStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        final List<Map<String, String>> expectedTableParameter = new ArrayList<Map<String, String>>();

        final Map<String, String> row = new HashMap<String, String>();
        expectedTableParameter.add(row);

        row.put("column1", "one");
        row.put("column2", "two");

        verify(spy, times(1)).methodWithTableArgument(expectedTableParameter);

        final ExecutionNode root = runner.getRootExecutionNode();

        System.out.println(root.printTree());
    }


    @Test
    public void testNestedParameterPassing() {

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features/paramsToSubSteps.feature",
                "nested_params_bug",
                "./target/test-classes/substeps/nested_params_substeps", null);

        final TestStepImplementations stepImpls = new TestStepImplementations();
        final TestStepImplementations spy = spy(stepImpls);

        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(TestStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        verify(spy, times(2)).sendKeysById("Basic", "firstName");
    }


    @Test
    public void testRunFeaturesInFolders() {
        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(TestStepImplementations.class);

        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features_dir", null,
                "./target/test-classes/substeps_dir", null);

        final TestStepImplementations stepImpls = new TestStepImplementations();
        final TestStepImplementations spy = spy(stepImpls);

        // get hold of the step runner
        // implsCache.put(execImpl.implementedIn, target);
        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(TestStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        verify(spy, times(1)).meth1("folder1");

        verify(spy, times(1)).meth2("bob");
    }


    @Test
    public void testRunWithNoTags() {
        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(MockStepImplementations.class);

        // pass in the stuff that would normally be placed in the annotation
        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features/allFeatures.feature", null,
                "./target/test-classes/substeps/allFeatures.substeps", null);

        final MockStepImplementations stepImpls = new MockStepImplementations();
        final MockStepImplementations spy = spy(stepImpls);

        // get hold of the step runner
        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(MockStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        final Description rootDescription = runner.getDescription();

        runner.run(notifier);

        // now verify that what was run was indeed run

        verify(spy, times(8)).meth1();

        verify(spy, times(1)).meth12();

        verify(spy, times(3)).meth2();

        verify(spy, times(1)).meth4("quoted parameter");
        verify(spy, times(1)).meth4("bob");
        verify(spy, times(1)).meth4("fred");
        verify(spy, times(1)).meth4("barf");

        verify(spy, never()).meth5();

        // substep verifications

        verify(spy, times(1)).meth7("something");
        verify(spy, times(1)).meth6();

        // test the quoted params
        verify(spy, times(1)).meth8("something in quotes");
        verify(spy, times(1)).meth9();

        final List<Map<String, String>> table = new ArrayList<Map<String, String>>();
        final Map<String, String> row = new HashMap<String, String>();
        row.put("param1", "W");
        row.put("param2", "X");
        row.put("param3", "Y");
        row.put("param4", "Z");
        table.add(row);

        verify(spy, times(1)).meth10(table);

        // test the number of times the notifier was called

        verify(notifier, times(27)).fireTestStarted(
                argThat(any(Description.class)));
        // this is now up to 25 as more of a hierarchy with outlines

        verify(notifier, times(22)).fireTestFinished(
                argThat(any(Description.class)));
        verify(notifier, times(5)).fireTestFailure(argThat(any(Failure.class)));
        // test failures now cascade upwards

        verify(spy, times(1)).meth4("#quoted parameter");

        final ExecutionNode root = runner.getRootExecutionNode();
        System.out.println(root.printTree());

    }


    @Test
    public void testNotifications() {

        System.out.println("\n\n\n\n\n: testNotifications");

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(MockStepImplementations.class);

        // pass in the stuff that would normally be placed in the annotation
        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features/notifications.feature", null,
                "./target/test-classes/substeps/allFeatures.substeps", null);

        final MockStepImplementations stepImpls = new MockStepImplementations();
        final MockStepImplementations spy = spy(stepImpls);

        // get hold of the step runner
        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(MockStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        final Description rootDescription = runner.getDescription();

        runner.run(notifier);

        // test the number of times the notifier was called

        verifyNotifications(notifier, rootDescription);

        verify(notifier, never()).fireTestFailure(argThat(any(Failure.class)));

    }


    /**
     * @param notifier
     * @param rootDescription
     */
    private void verifyNotifications(final RunNotifier notifier,
            final Description rootDescription) {
        int idx = 1;

        verify(notifier, times(1)).fireTestStarted(rootDescription);
        verify(notifier, times(1)).fireTestFinished(rootDescription);

        System.out.println("root fire count: " + idx++);

        final ArrayList<Description> features = rootDescription.getChildren();
        for (final Description fDesc : features) {
            System.out.println("feature fire count: " + idx++);

            verify(notifier, times(1)).fireTestStarted(fDesc);
            verify(notifier, times(1)).fireTestFinished(fDesc);

            final ArrayList<Description> scenarios = fDesc.getChildren();
            for (final Description scDesc : scenarios) {
                System.out.println("scenario fire count: " + idx++);

                verify(notifier, times(1)).fireTestStarted(scDesc);
                verify(notifier, times(1)).fireTestFinished(scDesc);

                final ArrayList<Description> steps = scDesc.getChildren();
                for (final Description step : steps) {
                    System.out.println("step fire count: " + idx++);

                    verify(notifier, times(1)).fireTestStarted(step);
                    verify(notifier, times(1)).fireTestFinished(step);
                }
            }
        }
    }


    @Test
    public void testStepWithInlineTable() {

        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(MockStepImplementations.class);

        // pass in the stuff that would normally be placed in the annotation
        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features/stepWithInlineTable.feature",
                null, "./target/test-classes/substeps/allFeatures.substeps",
                null);

        final MockStepImplementations stepImpls = new MockStepImplementations();
        final MockStepImplementations spy = spy(stepImpls);

        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(MockStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        // now verify that what was run was indeed run

        final List<Map<String, String>> table = new ArrayList<Map<String, String>>();
        final Map<String, String> row = new HashMap<String, String>();
        row.put("param1", "W");
        row.put("param2", "X");
        row.put("param3", "Y");
        row.put("param4", "Z");
        table.add(row);

        verify(spy, times(1)).meth10(table);
    }


    @Test
    public void testStepImplConstruction() {
        final StepImplementation si = StepImplementation.parse(
                "Given it is Christmas", this.getClass(), null);

        Assert.assertNotNull(si);
        Assert.assertThat(si.getValue(), is("Given it is Christmas"));
        Assert.assertThat(si.getKeyword(), is("Given"));

    }


    @Test
    public void testSubStepFailureHandledCorrectly() {
        final JunitFeatureRunner runner = new JunitFeatureRunner();

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(MockStepImplementations.class);

        // pass in the stuff that would normally be placed in the annotation
        runner.init(this.getClass(), stepImplsList,
                "./target/test-classes/features/bugs.feature", "@bug1",
                "./target/test-classes/substeps/bugs.substeps", null);

        final MockStepImplementations stepImpls = new MockStepImplementations();
        final MockStepImplementations spy = spy(stepImpls);

        // get hold of the step runner

        final Map<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(MockStepImplementations.class, spy);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        // now verify that what was run was indeed run

        verify(spy, times(1)).meth12();

        verify(spy, times(1)).meth11();

        verify(spy, never()).meth9();
        verify(spy, never()).meth6();

        verify(notifier, times(6)).fireTestStarted(
                argThat(any(Description.class)));

        verify(notifier, times(5)).fireTestFailure(argThat(any(Failure.class)));

    }

}
