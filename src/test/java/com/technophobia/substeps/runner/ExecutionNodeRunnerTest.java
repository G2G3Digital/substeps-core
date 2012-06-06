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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;
import com.technophobia.substeps.steps.TestStepImplementations;


/**
 * @author ian
 * 
 */
public class ExecutionNodeRunnerTest {
    
	@Test
	public void testParseErrorResultsInFailedTest(){

		// a missing substep

        final String feature = "./target/test-classes/features/error.feature";
        final String tags = "@bug_missing_sub_step_impl";
        final String substeps = "./target/test-classes/substeps/error.substeps";
        final INotifier notifier = mock(INotifier.class);
		
		final ExecutionNode rootNode = runExecutionTest(feature, tags, substeps, notifier);
        
        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        
        final ExecutionNode featureNode = rootNode.getChild(0);
        final ExecutionNode scenarioNode = featureNode.getChild(0);
        
        Assert.assertThat(scenarioNode.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));
        
        verify(notifier, times(1)).notifyTestFailed(eq(scenarioNode), argThat(any(SubStepConfigurationException.class)));
	}
	
	@Test
	public void testSubStepDefinitionMatchesStepImplFailure(){

        final String feature = "./target/test-classes/features/error3.feature";
        final String tags = "@duplicate_step_step_def";
        final String substeps = "./target/test-classes/substeps/duplicates2.substeps";
        final INotifier notifier = mock(INotifier.class);
		
		final ExecutionNode rootNode = runExecutionTest(feature, tags, substeps, notifier);
        
        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        
        final ExecutionNode featureNode = rootNode.getChild(0);
        final ExecutionNode scenarioNode = featureNode.getChild(0);
        
        Assert.assertThat(scenarioNode.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));
        
        verify(notifier, times(1)).notifyTestFailed(eq(scenarioNode), argThat(any(SubStepConfigurationException.class)));

	}

	@Ignore("can't get to fail as I would expect for some reason")
	@Test
	public void testParseError2ResultsInFailedTest(){

		// an example outline with null values

        final String feature = "./target/test-classes/features/error2.feature";
        final String tags = "@invalid_scenario_outline";
        final String substeps = "./target/test-classes/substeps/simple.substeps";
        final INotifier notifier = mock(INotifier.class);
		
		final ExecutionNode rootNode = runExecutionTest(feature, tags, substeps, notifier);
        
		System.out.println("\n\n\n\n\n*************\n\n" + rootNode.toDebugString());
		
        // check the rootNode tree is in the state we expect
        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        
        final ExecutionNode featureNode = rootNode.getChild(0);
        final ExecutionNode scenarioNode = featureNode.getChild(0); 
        
        final ExecutionNode scenarioOutlineNode2 = scenarioNode.getChild(1);
        
        Assert.assertThat(scenarioOutlineNode2.getResult().getResult(), is(ExecutionResult.PARSE_FAILURE));
        
        verify(notifier, times(1)).notifyTestFailed(eq(scenarioOutlineNode2), argThat(any(SubStepConfigurationException.class)));
	}

	@Test
	public void regExTest()
	{
	//	replacing: <message> with: You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number. in string: Then a method with a quoted '<message>'
		
		String rtn = "Then a method with a quoted '<message>'";
		final String key = "message";
		final String val = "You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number.";
		
		rtn = rtn.replaceAll("<" + key + ">", Matcher.quoteReplacement(val));
		
//	    rtn = Pattern.compile("<" + key + ">").matcher(rtn).replaceAll(Matcher.quoteReplacement(val));
		
		Assert.assertThat(rtn, is("Then a method with a quoted 'You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number.'"));
	
	}
	
	
	
	/**
	 * @param feature
	 * @param tags
	 * @param substeps
	 * @param notifier
	 * @return
	 */
	private ExecutionNode runExecutionTest(final String feature, final String tags, final String substeps,
			final INotifier notifier)
	{
		final ExecutionConfig executionConfig = new ExecutionConfig();

		executionConfig.setTags(tags);
        executionConfig.setFeatureFile(feature);
        executionConfig.setSubStepsFileName(substeps);
        
        final List<Class<?>> stepImplementationClasses = new ArrayList<Class<?>>();
        stepImplementationClasses.add(TestStepImplementations.class);
        
        executionConfig.setStepImplementationClasses(stepImplementationClasses);

        // this results in test failure rather than exception
        executionConfig.setFastFailParseErrors(false);
        
        final ExecutionNodeRunner runner = new ExecutionNodeRunner();
        
        
        final ExecutionNode rootNode = runner.prepareExecutionConfig(executionConfig, notifier);
        
        runner.run();
		return rootNode;
	}
	
	private void setPrivateField(final Object target, final String fieldName, final Object value){
		
		Field field;
		try
		{
			field = target.getClass().getDeclaredField(fieldName);
			final boolean currentAccessibility = field.isAccessible();

			field.setAccessible(true);
			
			field.set(target, value);
			
			field.setAccessible(currentAccessibility);
		}
		catch (final SecurityException e)
		{
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		catch (final NoSuchFieldException e)
		{
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e)
		{
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@Test
    public void testNoTestsExecutedResultsInFailure() {
        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final ExecutionNode node = new ExecutionNode();

        final IJunitNotifier notifier = spy(new JunitNotifier());

        final Map<Long, Description> descriptionMap = new HashMap<Long, Description>();

        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);

        final Description d = mock(Description.class);

        descriptionMap.put(Long.valueOf(node.getId()), d);
        notifier.setDescriptionMap(descriptionMap);

        
        setPrivateField(runner, "rootNode", node);
        setPrivateField(runner, "notifier", notifier);
        setPrivateField(runner, "setupAndTearDown", setupAndTearDown);
        
        runner.run();

        verify(notifier, times(1)).notifyTestFailed(argThat(is(d)),
                argThat(any(IllegalStateException.class)));
    }


    @Test
    public void testScenarioOutlineFailsWithNoExamples() {
        final ExecutionNode rootNode = new ExecutionNode();

        // add a feature
        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature("test feature", "file");
        featureNode.setFeature(feature);
        rootNode.addChild(featureNode);

        // add a scenario outline
        final ExecutionNode scenarioNode = new ExecutionNode();
        scenarioNode.setScenarioName("scenarioName");
        featureNode.addChild(scenarioNode);
        scenarioNode.setOutline(true);

        // this would add some outline nodes
        // ExecutionNode scenarioOutlineNode = new ExecutionNode();
        // scenarioNode.addChild(scenarioOutlineNode);
        //
        // scenarioOutlineNode.setRowNumber(idx);

        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        final IJunitNotifier notifier = spy(new JunitNotifier());

        final Map<Long, Description> descriptionMap = new HashMap<Long, Description>();

        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);

        final Description rootD = mock(Description.class);
        final Description featureD = mock(Description.class);
        final Description sceanrioD = mock(Description.class);

        descriptionMap.put(rootNode.getLongId(), rootD);
        descriptionMap.put(featureNode.getLongId(), featureD);
        descriptionMap.put(scenarioNode.getLongId(), sceanrioD);

        notifier.setDescriptionMap(descriptionMap);

        setPrivateField(runner, "rootNode", rootNode);
        setPrivateField(runner, "notifier", notifier);
        setPrivateField(runner, "setupAndTearDown", setupAndTearDown);

        runner.run();

        // the failure is called on the root twice, once for the child not
        // having tests, the other for
        // not having any run any tests
        verify(notifier, times(2)).notifyTestFailed(argThat(is(rootD)),
                argThat(any(Throwable.class)));
        verify(notifier, times(1)).notifyTestFailed(argThat(is(featureD)),
                argThat(any(Throwable.class)));
        verify(notifier, times(1)).notifyTestFailed(argThat(is(sceanrioD)),
                argThat(any(Throwable.class)));
    }


    // TODO WIP
    @Test
    public void testStepFailureFailsFeature() {

        final ExecutionNode rootNode = new ExecutionNode();

        // add a feature
        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature("test feature", "file");
        featureNode.setFeature(feature);
        rootNode.addChild(featureNode);

        // add a scenario outline
        final ExecutionNode scenarioNode = new ExecutionNode();
        scenarioNode.setScenarioName("scenarioName");
        featureNode.addChild(scenarioNode);
        scenarioNode.setOutline(true);

        final ExecutionNode scenarioOutlineNode = new ExecutionNode();
        scenarioNode.addChild(scenarioOutlineNode);
        scenarioOutlineNode.setRowNumber(1);

        final ExecutionNode scenarioOutlineNode2 = new ExecutionNode();
        scenarioNode.addChild(scenarioOutlineNode2);
        scenarioOutlineNode2.setRowNumber(2);

        Method nonFailMethod = null;
        Method failMethod = null;
        try {
            nonFailMethod = this.getClass().getMethod("nonFailingMethod");
            failMethod = this.getClass().getMethod("failingMethod");
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(nonFailMethod);
        Assert.assertNotNull(failMethod);

        final ExecutionNode stepNode1 = new ExecutionNode();
        scenarioOutlineNode.addChild(stepNode1);
        stepNode1.setTargetClass(this.getClass());
        stepNode1.setTargetMethod(nonFailMethod);

        final ExecutionNode stepNode2 = new ExecutionNode();
        scenarioOutlineNode.addChild(stepNode2);
        stepNode2.setTargetClass(this.getClass());
        stepNode2.setTargetMethod(failMethod);

        final ExecutionNode stepNode3 = new ExecutionNode();
        scenarioOutlineNode.addChild(stepNode3);
        stepNode3.setTargetClass(this.getClass());
        stepNode3.setTargetMethod(nonFailMethod);

        final ExecutionNode stepNode1b = new ExecutionNode();
        scenarioOutlineNode2.addChild(stepNode1b);
        stepNode1b.setTargetClass(this.getClass());
        stepNode1b.setTargetMethod(nonFailMethod);

        final ExecutionNode stepNode2b = new ExecutionNode();
        scenarioOutlineNode2.addChild(stepNode2b);
        stepNode2b.setTargetClass(this.getClass());
        stepNode2b.setTargetMethod(nonFailMethod);

        final ExecutionNode stepNode3b = new ExecutionNode();
        scenarioOutlineNode2.addChild(stepNode3b);
        stepNode3b.setTargetClass(this.getClass());
        stepNode3b.setTargetMethod(nonFailMethod);

        final INotifier notifier = mock(INotifier.class);
        final SetupAndTearDown setupAndTearDown = mock(SetupAndTearDown.class);
        final ExecutionNodeRunner runner = new ExecutionNodeRunner();

        setPrivateField(runner, "rootNode", rootNode);
        setPrivateField(runner, "notifier", notifier);
        setPrivateField(runner, "setupAndTearDown", setupAndTearDown);

        runner.run();

        Assert.assertThat(rootNode.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(featureNode.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(scenarioNode.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(scenarioOutlineNode.getResult().getResult(), is(ExecutionResult.FAILED));

        Assert.assertThat(stepNode1.getResult().getResult(), is(ExecutionResult.PASSED));
        Assert.assertThat(stepNode2.getResult().getResult(), is(ExecutionResult.FAILED));
        Assert.assertThat(stepNode3.getResult().getResult(), is(ExecutionResult.NOT_RUN));

        Assert.assertThat(stepNode1b.getResult().getResult(), is(ExecutionResult.PASSED));
        Assert.assertThat(stepNode2b.getResult().getResult(), is(ExecutionResult.PASSED));
        Assert.assertThat(stepNode3b.getResult().getResult(), is(ExecutionResult.PASSED));

    }


    public void nonFailingMethod() {
        System.out.println("no fail");
    }


    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }
}
