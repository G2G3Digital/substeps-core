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
package com.technophobia.substeps.report;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.Feature;



/**
 * @author ian
 *
 */
public class DefaultExecutionReportBuilderTest
{
    public void nonFailingMethod() {
        System.out.println("no fail");
    }


    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }
	
	private ReportData getData(){
		
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

		
		final ReportData data = new ReportData();
		
		final ExecutionNode rootNode = new ExecutionNode();
		
        // add a feature
        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature("test feature", "file");
        featureNode.setFeature(feature);
        rootNode.addChild(featureNode);

        final ExecutionNode scenarioNode = new ExecutionNode();
        scenarioNode.setScenarioName("scenarioName");
        featureNode.addChild(scenarioNode);
        scenarioNode.setOutline(true);

//        final ExecutionNode scenarioOutlineNode = new ExecutionNode();
//        scenarioNode.addChild(scenarioOutlineNode);
//        scenarioOutlineNode.setRowNumber(1);
//
//        final ExecutionNode scenarioOutlineNode2 = new ExecutionNode();
//        scenarioNode.addChild(scenarioOutlineNode2);
//        scenarioOutlineNode2.setRowNumber(2);
        
        final ExecutionNode stepNode1 = new ExecutionNode();
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
        
        final ExecutionNode stepNode3 = new ExecutionNode();
        scenarioNode.addChild(stepNode3);
//        scenarioOutlineNode.addChild(stepNode3);

        stepNode3.setTargetClass(this.getClass());
        stepNode3.setTargetMethod(nonFailMethod);
        stepNode3.setLine("stepNode3");
        
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
		
		data.addRootExecutionNode(rootNode);
		
		return data;
	}
	
	@Ignore("manual test to build the reports")
	@Test
	public void testReportBuilding(){
		
		final DefaultExecutionReportBuilder builder = new DefaultExecutionReportBuilder();
		
//        final DescriptorStatus status = new DescriptorStatus();
        final ReportData data = getData();
//        data.setStatus(status);
        
        final ExecutionNode root = data.getNodeList().get(0);
		
        final StringBuilder buf = new StringBuilder();
        
		builder.buildTreeString(buf, root, data);
		
		// TODO could check that this is well formed xhtml with unit or compare it to the html that's desired
		System.out.println("\n\n\n\n" + buf.toString() + "\n\n\n\n");
		
//		System.out.println(root.treeToString());
//		
//		System.out.println("\n\n\n\n" );
//		
//		for (final ExecutionNode flattened : data.getNodeList())
//		{
//			
//			System.out.println(data.getAddTreeNodeStr(flattened));
//		}
	}
}
