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
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.execution.node.TestBasicScenarioNodeBuilder;
import com.technophobia.substeps.execution.node.TestFeatureNodeBuilder;
import com.technophobia.substeps.execution.node.TestOutlineScenarioNodeBuilder;
import com.technophobia.substeps.execution.node.TestOutlineScenarioRowNodeBuilder;
import com.technophobia.substeps.execution.node.TestRootNodeBuilder;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.model.exception.UnimplementedStepException;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;
import com.technophobia.substeps.steps.TestStepImplementations;

/**
 * @author ian
 * 
 */
public class ExecutionNodeRunnerTest {

    @Test
    public void testScenarioStepWithParameters() {

        // this failure used to be more dramatic - now the parameter name is
        // passed instead - not such a big failure

        final String feature = "./target/test-classes/features/error4.feature";
        final String tags = "scenario_with_params";
        final String substeps = "./target/test-classes/substeps/simple.substeps";
        final INotifier notifier = mock(INotifier.class);

        final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();

        final RootNode rootNode = runExecutionTest(feature, tags, substeps, notifier, failures);

        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.PASSED));

    }

    @Test
    public void testParseErrorResultsInFailedTest() {

        // a missing substep

        final String feature = "./target/test-classes/features/error.feature";
        final String tags = "@bug_missing_sub_step_impl";
        final String substeps = "./target/test-classes/substeps/error.substeps";
        final INotifier notifier = mock(INotifier.class);

        final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();

        final RootNode rootNode = runExecutionTest(feature, tags, substeps, notifier, failures);

        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));

        final FeatureNode featureNode = rootNode.getChildren().get(0);
        final ScenarioNode<?> scenarioNode = featureNode.getChildren().get(0);

        Assert.assertThat(scenarioNode.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));

        verify(notifier, times(1)).notifyNodeFailed(eq(scenarioNode), argThat(any(UnimplementedStepException.class)));

        Assert.assertThat(failures.size(), is(2));

        Assert.assertThat(failures.get(0).getCause(), instanceOf(UnimplementedStepException.class));
        Assert.assertThat(failures.get(0).getCause().getMessage(),
                is("SingleWord is not a recognised step or substep implementation"));

        Assert.assertThat(failures.get(1).getCause(), instanceOf(IllegalStateException.class));
        Assert.assertThat(failures.get(1).getCause().getMessage(), is("No tests executed"));

    }

    @Test
    public void testSubStepDefinitionMatchesStepImplFailure() {

        final String feature = "./target/test-classes/features/error3.feature";
        final String tags = "@duplicate_step_step_def";
        final String substeps = "./target/test-classes/substeps/duplicates2.substeps";
        final INotifier notifier = mock(INotifier.class);

        final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();

        final RootNode rootNode = runExecutionTest(feature, tags, substeps, notifier, failures);

        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));

        final FeatureNode featureNode = rootNode.getChildren().get(0);
        final ScenarioNode<?> scenarioNode = featureNode.getChildren().get(0);

        Assert.assertThat(scenarioNode.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));

        verify(notifier, times(1)).notifyNodeFailed(eq(scenarioNode),
                argThat(any(SubstepsConfigurationException.class)));

        Assert.assertThat(failures.size(), is(2));

        Assert.assertThat(failures.get(0).getCause(), instanceOf(SubstepsConfigurationException.class));

        Assert.assertThat(
                failures.get(0).getCause().getMessage(),
                is("line: [Given something] in [./target/test-classes/features/error3.feature] matches step implementation method: [public void com.technophobia.substeps.steps.TestStepImplementations.given()] AND matches a sub step definition: [Given something] in [duplicates2.substeps]"));

        Assert.assertThat(failures.get(1).getCause(), instanceOf(IllegalStateException.class));
        Assert.assertThat(failures.get(1).getCause().getMessage(), is("No tests executed"));

    }

    @Ignore("can't get to fail as I would expect for some reason")
    @Test
    public void testParseError2ResultsInFailedTest() {

        // an example outline with null values

        final String feature = "./target/test-classes/features/error2.feature";
        final String tags = "@invalid_scenario_outline";
        final String substeps = "./target/test-classes/substeps/simple.substeps";
        final INotifier notifier = mock(INotifier.class);

        // TODO - checkfailures - test currently ignored anyway..
        final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();
        final RootNode rootNode = runExecutionTest(feature, tags, substeps, notifier, failures);

        System.out.println("\n\n\n\n\n*************\n\n" + rootNode.toDebugString());

        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));

        final FeatureNode featureNode = rootNode.getChildren().get(0);

        final OutlineScenarioNode scenarioOutlineNode2 = (OutlineScenarioNode) featureNode.getChildren().get(1);

        Assert.assertThat(scenarioOutlineNode2.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));

        verify(notifier, times(1)).notifyNodeFailed(eq(scenarioOutlineNode2),
                argThat(any(SubstepsConfigurationException.class)));

    }

    @Test
    public void regExTest() {
        // replacing: <message> with: You must enter the following information
        // to proceed:$Sort code.$Bank Account Name.$Bank Account Number. in
        // string: Then a method with a quoted '<message>'

        String rtn = "Then a method with a quoted '<message>'";
        final String key = "message";
        final String val = "You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number.";

        rtn = rtn.replaceAll("<" + key + ">", Matcher.quoteReplacement(val));

        // rtn = Pattern.compile("<" + key +
        // ">").matcher(rtn).replaceAll(Matcher.quoteReplacement(val));

        Assert.assertThat(
                rtn,
                is("Then a method with a quoted 'You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number.'"));

    }

    /**
     * @param feature
     * @param tags
     * @param substeps
     * @param notifier
     * @return
     */
    private RootNode runExecutionTest(final String feature, final String tags, final String substeps,
            final INotifier notifier, final Class<?>[] initialisationClasses,
            final List<SubstepExecutionFailure> failures) {
        final SubstepsExecutionConfig executionConfig = new SubstepsExecutionConfig();

        Assert.assertTrue(failures.isEmpty());

        executionConfig.setTags(tags);
        executionConfig.setFeatureFile(feature);
        executionConfig.setSubStepsFileName(substeps);

        final List<Class<?>> stepImplementationClasses = new ArrayList<Class<?>>();
        stepImplementationClasses.add(TestStepImplementations.class);

        executionConfig.setStepImplementationClasses(stepImplementationClasses);

        // this results in test failure rather than exception
        executionConfig.setFastFailParseErrors(false);

        if (initialisationClasses != null) {
            executionConfig.setInitialisationClasses(initialisationClasses);
        }

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();
        runner.addNotifier(notifier);

        runner.prepareExecutionConfig(executionConfig);

        final RootNode rootNode = runner.run();

        final List<SubstepExecutionFailure> localFailures = runner.getFailures();

        failures.addAll(localFailures);

        return rootNode;
    }

    private RootNode runExecutionTest(final String feature, final String tags, final String substeps,
            final INotifier notifier, final List<SubstepExecutionFailure> failures) {

        return runExecutionTest(feature, tags, substeps, notifier, null, failures);
    }

    private void setPrivateField(final Object target, final String fieldName, final Object value) {

        Field field;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            final boolean currentAccessibility = field.isAccessible();

            field.setAccessible(true);

            field.set(target, value);

            field.setAccessible(currentAccessibility);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(final Object object, final String fieldName) {

        Field field;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
            return null; // Unreachable
        }

    }

    /**
     * If we use a root node with no children, we should get two failures, one
     * for there being no children on a node which should have children, another
     * for there being no tests run
     */
    @Test
    public void testNoTestsExecutedResultsInTwoFailures() {

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final RootNode node = new RootNode("Description", Collections.<FeatureNode> emptyList());

        final INotificationDistributor notificationDistributor = getPrivateField(runner, "notificationDistributor");
        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);

        final RootNodeExecutionContext nodeExecutionContext = new RootNodeExecutionContext(notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, null, new ImplementationCache());

        setPrivateField(runner, "rootNode", node);
        setPrivateField(runner, "nodeExecutionContext", nodeExecutionContext);

        final INotifier mockNotifer = mock(INotifier.class);
        runner.addNotifier(mockNotifer);

        runner.run();
        final List<SubstepExecutionFailure> failures = runner.getFailures();

        verify(mockNotifer, times(2)).notifyNodeFailed(argThat(is(node)), argThat(any(IllegalStateException.class)));

        Assert.assertFalse("expecting some failures", failures.isEmpty());

        Assert.assertThat(failures.size(), is(2));
    }

    @Test
    public void testScenarioOutlineFailsWithNoExamples() {

        final OutlineScenarioNode outlineNode = new OutlineScenarioNode("scenarioName",
                Collections.<OutlineScenarioRowNode> emptyList(), Collections.<String> emptySet(), 2);
        final FeatureNode featureNode = new FeatureNode(new Feature("test feature", "file"),
                Collections.<ScenarioNode<?>> singletonList(outlineNode), Collections.<String> emptySet());
        final ExecutionNode rootNode = new RootNode("Description", Collections.singletonList(featureNode));

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final INotificationDistributor notificationDistributor = getPrivateField(runner, "notificationDistributor");
        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);
        final RootNodeExecutionContext nodeExecutionContext = new RootNodeExecutionContext(notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, null, new ImplementationCache());

        setPrivateField(runner, "rootNode", rootNode);
        setPrivateField(runner, "nodeExecutionContext", nodeExecutionContext);

        final INotifier mockNotifer = mock(INotifier.class);
        runner.addNotifier(mockNotifer);

        runner.run();
        final List<SubstepExecutionFailure> failures = runner.getFailures();

        // the failure is called on the root twice, once for the child not
        // having tests, the other for
        // not having any run any tests
        // NB. notifications of failed nodes not the same as the actual failure
        // list returned
        // list contains just those nodes that have actually failed, not the
        // entire tree.

        verify(mockNotifer, times(2)).notifyNodeFailed(argThat(is(rootNode)), argThat(any(Throwable.class)));

        verify(mockNotifer, times(1)).notifyNodeFailed(argThat(is(featureNode)), argThat(any(Throwable.class)));

        verify(mockNotifer, times(1)).notifyNodeFailed(argThat(is(outlineNode)), argThat(any(Throwable.class)));

        Assert.assertFalse("expecting some failures", failures.isEmpty());

        // two failures, one for the scenario outline not having any examples,
        // other for the root node for not having run any tests
        Assert.assertThat(failures.size(), is(2));

        Assert.assertThat(failures.size(), is(2));

        Assert.assertThat(failures.get(0).getCause(), instanceOf(IllegalStateException.class));

        Assert.assertThat(failures.get(0).getCause().getMessage(), is("node should have children but doesn't"));

        Assert.assertThat(failures.get(1).getCause(), instanceOf(IllegalStateException.class));
        Assert.assertThat(failures.get(1).getCause().getMessage(), is("No tests executed"));
    }

    private Method getNonFailMethod() {

        return getMethodOrFail("nonFailingMethod");
    }

    private Method getFailMethod() {

        return getMethodOrFail("failingMethod");
    }

    private Method getMethodOrFail(final String method) {

        try {

            return this.getClass().getMethod(method);

        } catch (final Exception e) {

            Assert.fail(e.getMessage());
            return null; // Unreachable
        }
    }

    @Test
    public void testStepFailureFailsFeature() {

        final Method nonFailMethod = getNonFailMethod();
        final Method failMethod = getFailMethod();
        Assert.assertNotNull(nonFailMethod);
        Assert.assertNotNull(failMethod);

        final String scenarioName = "scenarioName";
        final TestRootNodeBuilder rootNodeBuilder = new TestRootNodeBuilder();
        final TestFeatureNodeBuilder featureBuilder = rootNodeBuilder.addFeature(new Feature("test feature", "file"));

        final TestOutlineScenarioNodeBuilder outlineScenarioBuilder = featureBuilder.addOutlineScenario(scenarioName);
        final TestOutlineScenarioRowNodeBuilder rowBuilder1 = outlineScenarioBuilder.addRow(1);
        final TestOutlineScenarioRowNodeBuilder rowBuilder2 = outlineScenarioBuilder.addRow(2);

        final TestBasicScenarioNodeBuilder row1ScenarioBuilder = rowBuilder1.setBasicScenario(scenarioName);
        row1ScenarioBuilder.addStepImpl(getClass(), nonFailMethod).addStepImpl(getClass(), failMethod)
                .addStepImpl(getClass(), nonFailMethod);
        final TestBasicScenarioNodeBuilder row2ScenarioBuilder = rowBuilder2.setBasicScenario(scenarioName);
        row2ScenarioBuilder.addStepImpls(3, getClass(), nonFailMethod);

        final RootNode rootNode = rootNodeBuilder.build();

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final INotificationDistributor notificationDistributor = getPrivateField(runner, "notificationDistributor");
        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);
        final RootNodeExecutionContext nodeExecutionContext = new RootNodeExecutionContext(notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, null, new ImplementationCache());

        setPrivateField(runner, "rootNode", rootNode);
        setPrivateField(runner, "nodeExecutionContext", nodeExecutionContext);

        runner.run();
        final List<SubstepExecutionFailure> failures = runner.getFailures();

        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(featureBuilder.getBuilt().getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(row1ScenarioBuilder.getBuilt().getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(row2ScenarioBuilder.getBuilt().getResult().getResult(), is(ExecutionResult.PASSED));
        Assert.assertThat(rowBuilder1.getBuilt().getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(rowBuilder2.getBuilt().getResult().getResult(), is(ExecutionResult.PASSED));

        Assert.assertThat(outlineScenarioBuilder.getBuilt().getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(row1ScenarioBuilder.getBuilt().getChildren().get(0).getResult().getResult(),
                is(ExecutionResult.PASSED));
        Assert.assertThat(row1ScenarioBuilder.getBuilt().getChildren().get(1).getResult().getResult(),
                is(ExecutionResult.FAILED));
        Assert.assertThat(row1ScenarioBuilder.getBuilt().getChildren().get(2).getResult().getResult(),
                is(ExecutionResult.NOT_RUN));

        Assert.assertThat(row2ScenarioBuilder.getBuilt().getChildren().get(0).getResult().getResult(),
                is(ExecutionResult.PASSED));
        Assert.assertThat(row2ScenarioBuilder.getBuilt().getChildren().get(1).getResult().getResult(),
                is(ExecutionResult.PASSED));
        Assert.assertThat(row2ScenarioBuilder.getBuilt().getChildren().get(2).getResult().getResult(),
                is(ExecutionResult.PASSED));

        Assert.assertFalse("expecting some failures", failures.isEmpty());

        // just one failure for the actual step that failed
        Assert.assertThat(failures.size(), is(1));

    }

    @BeforeAllFeatures
    public void failingSetupMethod() {

        throw new IllegalStateException("something has gone wrong");
    }

    @Test
    public void testBeforeAllFeaturesSetupFailureFailsTheBuild() {

        final Method nonFailMethod = getNonFailMethod();
        final Method failMethod = getFailMethod();
        Assert.assertNotNull(nonFailMethod);
        Assert.assertNotNull(failMethod);

        final String scenarioName = "scenarioName";
        final TestRootNodeBuilder rootNodeBuilder = new TestRootNodeBuilder();
        final TestFeatureNodeBuilder featureBuilder = rootNodeBuilder.addFeature(new Feature("test feature", "file"));

        final TestOutlineScenarioNodeBuilder outlineScenarioBuilder = featureBuilder.addOutlineScenario(scenarioName);
        final TestOutlineScenarioRowNodeBuilder rowBuilder1 = outlineScenarioBuilder.addRow(1);
        final TestOutlineScenarioRowNodeBuilder rowBuilder2 = outlineScenarioBuilder.addRow(2);

        final TestBasicScenarioNodeBuilder row1ScenarioBuilder = rowBuilder1.setBasicScenario(scenarioName);
        row1ScenarioBuilder.addStepImpl(getClass(), nonFailMethod).addStepImpl(getClass(), failMethod)
                .addStepImpl(getClass(), nonFailMethod);
        final TestBasicScenarioNodeBuilder row2ScenarioBuilder = rowBuilder2.setBasicScenario(scenarioName);
        row2ScenarioBuilder.addStepImpl(getClass(), nonFailMethod).addStepImpls(3, getClass(), failMethod);

        final RootNode rootNode = rootNodeBuilder.build();

        final Class<?>[] setupClasses = new Class[] { this.getClass() };
        final SetupAndTearDown setupAndTearDown = new SetupAndTearDown(setupClasses, new ImplementationCache());

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final INotificationDistributor notificationDistributor = getPrivateField(runner, "notificationDistributor");
        final RootNodeExecutionContext nodeExecutionContext = new RootNodeExecutionContext(notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, null, new ImplementationCache());

        setPrivateField(runner, "rootNode", rootNode);
        setPrivateField(runner, "nodeExecutionContext", nodeExecutionContext);

        runner.run();
        final List<SubstepExecutionFailure> failures = runner.getFailures();

        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(featureBuilder.getBuilt().getResult().getResult(), is(ExecutionResult.NOT_RUN));

        Assert.assertFalse("expecting some failures", failures.isEmpty());

        // two failures - one for the @before failure and another because no
        // tests run
        Assert.assertThat(failures.size(), is(2));

        Assert.assertTrue("failure should be marked as setup or tear down", failures.get(0).isSetupOrTearDown());

        Assert.assertThat(failures.get(1).getCause(), instanceOf(IllegalStateException.class));

        Assert.assertThat(failures.get(1).getCause().getMessage(), is("No tests executed"));
    }

    public void nonFailingMethod() {
        System.out.println("no fail");
    }

    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }
}
