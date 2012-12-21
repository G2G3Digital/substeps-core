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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.execution.node.TestBasicScenarioNodeBuilder;
import com.technophobia.substeps.execution.node.TestFeatureNodeBuilder;
import com.technophobia.substeps.execution.node.TestOutlineScenarioNodeBuilder;
import com.technophobia.substeps.execution.node.TestOutlineScenarioRowNodeBuilder;
import com.technophobia.substeps.execution.node.TestRootNodeBuilder;
import com.technophobia.substeps.execution.node.TestSubstepNodeBuilder;

/**
 * <pre>
 * 
 * Given the following substep configuration
 * 
 * where SS = SubstepNode
 *       SI = StepImplementationNode
 * 
 * 
 *                            SS - SI
 *                            /    /
 *                          *SS - SS - SI
 *                          /
 *            *BasicScenarioA - *SS - SI
 *            /                   \    
 *      *Feature1                  SI
 *            \
 *            *BasicScenarioB - *SS - SI
 *                           \
 *                            *SI
 *                                                   SI
 *                                                  /
 *                                               *SS - SI
 *                                              /   
 *                                BasicScenarioC - *SS - SI   SI
 *                                /                          / 
 *                             Row1                       *SS - SI
 *                            /                           /
 *             *ScenarioOutline1 - Row2 - BasicScenarioC  - *SS - SI 
 *             /
 *      *Feature2
 *             \
 *             *ScenarioOutline2 - Row1 - BasicScenarioD - *SI
 *                           \
 *                           Row2
 *                              \
 *                               BasicScenarioD - *SI
 * 
 * The starred nodes should contribute to the counts.
 * 
 * Therefore we have
 * 
 * 2  features
 * 4  scenarios
 * 10 scenario steps
 * 
 * </pre>
 * 
 * @author rbarefield
 * 
 */
public class ExecutionStatsTest {

    private ReportData reportData;

    private static final int FEATURES_EXPECTED = 2;
    private static final int SCENARIOS_EXPECTED = 4;
    private static final int SCENARIO_STEPS_EXPECTED = 10;

    @Before
    public void setupDataAsAbove() {

        TestRootNodeBuilder rootBuilder = new TestRootNodeBuilder();

        TestFeatureNodeBuilder feature1 = rootBuilder.addFeature(new Feature("Feature1", "File"));
        TestFeatureNodeBuilder feature2 = rootBuilder.addFeature(new Feature("Feature2", "File"));

        TestBasicScenarioNodeBuilder scenarioA = feature1.addBasicScenario("ScenarioA");
        TestBasicScenarioNodeBuilder scenarioB = feature1.addBasicScenario("ScenarioB");

        TestSubstepNodeBuilder aSubstep1 = scenarioA.addSubstep();
        aSubstep1.addSubstep().addStepImpl(getClass(), null);
        aSubstep1.addSubstep().addStepImpl(getClass(), null);
        scenarioA.addSubstep().addStepImpl(getClass(), null).addStepImpl(getClass(), null);
        scenarioB.addStepImpl(getClass(), null).addSubstep().addStepImpl(getClass(), null);

        TestOutlineScenarioNodeBuilder scenarioOutline1 = feature2.addOutlineScenario("SO1");
        TestOutlineScenarioRowNodeBuilder row1 = scenarioOutline1.addRow(1);
        TestOutlineScenarioRowNodeBuilder row2 = scenarioOutline1.addRow(2);

        TestBasicScenarioNodeBuilder basicScenarioC = row1.setBasicScenario("BasicScenarioC");
        basicScenarioC.addSubstep().addStepImpls(2, getClass(), null);
        basicScenarioC.addSubstep().addStepImpl(getClass(), null);
        row2.setBasicScenario(basicScenarioC);

        TestOutlineScenarioNodeBuilder scenarioOutline2 = feature2.addOutlineScenario("SO2");

        TestOutlineScenarioRowNodeBuilder sO2Row1 = scenarioOutline2.addRow(0);

        TestBasicScenarioNodeBuilder basicScenarioD = sO2Row1.setBasicScenario("BasicScenarioD");
        basicScenarioD.addStepImpl(getClass(), null);

        TestOutlineScenarioRowNodeBuilder sO2Row2 = scenarioOutline2.addRow(1);
        sO2Row2.setBasicScenario(basicScenarioD);

        reportData = new ReportData();
        reportData.addRootExecutionNode(rootBuilder.build());

    }

    @Test
    public void testCorrectNodesAreUsed() {

        ExecutionStats executionStats = new ExecutionStats();
        executionStats.buildStats(reportData);

        Assert.assertEquals(FEATURES_EXPECTED, executionStats.getTotalFeatures());
        Assert.assertEquals(SCENARIOS_EXPECTED, executionStats.getTotalScenarios());
        Assert.assertEquals(SCENARIO_STEPS_EXPECTED, executionStats.getTotalScenarioSteps());
    }
}
