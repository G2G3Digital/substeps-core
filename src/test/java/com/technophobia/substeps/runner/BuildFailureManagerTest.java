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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.execution.Feature;


/**
 * @author ian
 *
 */
public class BuildFailureManagerTest
{
    public void nonFailingMethod() {
        System.out.println("no fail");
    }


    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }

	
	private ExecutionNode getData(){
		
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
		
		final ExecutionNode rootNode = new ExecutionNode();
		
		final Throwable rootFail = new Exception("t1");
		rootNode.getResult().setFailed(rootFail);
		
        // add a feature
        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature("test feature", "file");
        featureNode.getResult().setFailed(rootFail);
        
        featureNode.setFeature(feature);
        rootNode.addChild(featureNode);
        final Set<String> tags = new HashSet<String>();
        tags.add("@can_fail");
        
        featureNode.setTags(tags);
        
        
        final ExecutionNode scenarioNode = new ExecutionNode();
        scenarioNode.setScenarioName("scenarioName");
        scenarioNode.getResult().setFailed(rootFail);
        
        featureNode.addChild(scenarioNode);
        scenarioNode.setOutline(false);
        scenarioNode.setTags(tags);
//        final ExecutionNode scenarioOutlineNode = new ExecutionNode();
//        scenarioNode.addChild(scenarioOutlineNode);
//        scenarioOutlineNode.setRowNumber(1);
//
//        final ExecutionNode scenarioOutlineNode2 = new ExecutionNode();
//        scenarioNode.addChild(scenarioOutlineNode2);
//        scenarioOutlineNode2.setRowNumber(2);
        
        final ExecutionNode stepNode1 = new ExecutionNode();
        stepNode1.getResult().setFailed(rootFail);
        
//        scenarioOutlineNode.addChild(stepNode1);
        scenarioNode.addChild(stepNode1);

        stepNode1.setTargetClass(this.getClass());
        stepNode1.setTargetMethod(nonFailMethod);
        stepNode1.setLine("stepNode1");
        
        final ExecutionNode stepNode2 = new ExecutionNode();
//        scenarioOutlineNode.addChild(stepNode2);
        scenarioNode.addChild(stepNode2);

        stepNode2.setTargetClass(this.getClass());
        stepNode2.setTargetMethod(failMethod);
        stepNode2.setLine("stepNode2");
        stepNode2.getResult().setResult(ExecutionResult.NOT_RUN);
        
        final ExecutionNode stepNode3 = new ExecutionNode();
        scenarioNode.addChild(stepNode3);
//        scenarioOutlineNode.addChild(stepNode3);

        stepNode3.setTargetClass(this.getClass());
        stepNode3.setTargetMethod(nonFailMethod);
        stepNode3.setLine("stepNode3");
        stepNode3.getResult().setResult(ExecutionResult.NOT_RUN);
        
//        final ExecutionNode stepNode1b = new ExecutionNode();
//        scenarioOutlineNode2.addChild(stepNode1b);
//        stepNode1b.setTargetClass(this.getClass());
//        stepNode1b.setTargetMethod(nonFailMethod);
//        stepNode1b.setLine("stepNode1b");
//        
//        final ExecutionNode stepNode2b = new ExecutionNode();
//        scenarioOutlineNode2.addChild(stepNode2b);
//        stepNode2b.setTargetClass(this.getClass());
//        stepNode2b.setTargetMethod(nonFailMethod);
//        stepNode2b.setLine("stepNode2b");
//        
//        final ExecutionNode stepNode3b = new ExecutionNode();
//        scenarioOutlineNode2.addChild(stepNode3b);
//        stepNode3b.setTargetClass(this.getClass());
//        stepNode3b.setTargetMethod(nonFailMethod);
//        stepNode3b.setLine("stepNode3b");
        
        return rootNode;
	}
	
	@Test
	public void testNonCriticalFailures(){
		
		BuildFailureManager bfm = new BuildFailureManager();
		
		final ExecutionNode rootNode = getData();
		// set up the scenario
		
		final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();

		final Throwable rootFail = new Exception("t1");

        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature("test feature", "file");
        featureNode.getResult().setFailed(rootFail);
        
        featureNode.setFeature(feature);
        rootNode.addChild(featureNode);
		
        final SubstepExecutionFailure f1 = 
        		new SubstepExecutionFailure(rootFail, featureNode);
        
        f1.setNonCritical(true);

        failures.add(f1);
        
		
        bfm.sortFailures(failures);
        
        // just one non crit error
        Assert.assertFalse(bfm.testSuiteCompletelyPassed());
        Assert.assertTrue(bfm.testSuiteSomeFailures());
        Assert.assertFalse(bfm.testSuiteFailed());
		
		
		
		// see what happens with an @beforefailure
		failures.clear();
		
        failures.add(new SubstepExecutionFailure(rootFail, featureNode, true));
        bfm = new BuildFailureManager();
        bfm.sortFailures(failures);

        
        // just an @before fail
        Assert.assertFalse(bfm.testSuiteCompletelyPassed());
        Assert.assertTrue(bfm.testSuiteSomeFailures());
        Assert.assertTrue(bfm.testSuiteFailed());


		failures.clear();
        failures.add(new SubstepExecutionFailure(rootFail, featureNode));
        bfm = new BuildFailureManager();
        bfm.sortFailures(failures);

        // a normal critical fail
        Assert.assertFalse(bfm.testSuiteCompletelyPassed());
        Assert.assertTrue(bfm.testSuiteSomeFailures());
        Assert.assertTrue(bfm.testSuiteFailed());

	}
}
