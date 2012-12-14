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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;

/**
 * @author ian
 * 
 */
public class DefaultExecutionReportBuilderTest {

    private static final String STEP_NODE = "stepNode";
    private static final String SCENARIO_NAME = "scenarioName";
    private static final String RESULT = "result";
    private static final String DESCRIPTION = "description";
    private static final String FEATURE_NAME = "test feature";
    private static final String NOT_RUN = "NOT_RUN";
    private static final String PASSED = "PASSED";
    private static final String NODE_TYPE = "nodetype";

    private static final Logger LOG = Logger.getLogger(DefaultExecutionReportBuilderTest.class);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    RootNode rootNode;

    List<ExecutionNode> featureNodes = Lists.newArrayList();

    List<ExecutionNode> scenarioNodes = Lists.newArrayList();

    List<ExecutionNode> stepNodes = Lists.newArrayList();

    private Integer nodeIdOffset = Integer.MAX_VALUE;

    private String arrayCreationLine;

    private static final String DETAIL_PATTERN = "detail\\[(\\d+)\\]=(\\{.*\\});";
    private static final String ARRAY_CREATION_LINE = "var detail = new Array();";

    private Map<Integer, JsonObject> details;
    private DefaultExecutionReportBuilder builder;

    public void nonFailingMethod() {
        System.out.println("no fail");
    }

    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }

    @Before
    public void createData() {

        builder = new DefaultExecutionReportBuilder();
        builder.setOutputDirectory(testFolder.getRoot());

        rootNode = createRootNode();

        builder.addRootExecutionNode(rootNode);



    }

    private RootNode createRootNode() {

        return new RootNode(Collections.singletonList(createFeature(FEATURE_NAME, "test file")));
    }

    private FeatureNode createFeature(String name, String fileName) {

        final Feature feature = new Feature(name, fileName);
        final FeatureNode featureNode = new FeatureNode(feature, Collections.singletonList(createScenario(SCENARIO_NAME)));
        featureNodes.add(featureNode);

        return featureNode;
    }

    private ScenarioNode createScenario(String scenarioName) {

        SubstepNode stepImpl = createSubstepNode();
        ScenarioNode scenarioNode = new BasicScenarioNode(scenarioName, null, stepImpl);
        scenarioNodes.add(scenarioNode);
        return scenarioNode;
    }

    private SubstepNode createSubstepNode() {

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

        StepNode stepImpl1 = createStep(this.getClass(), nonFailMethod, STEP_NODE + "1");
        StepNode stepImpl2 = createStep(this.getClass(), failMethod, STEP_NODE + "2");
        StepNode stepImpl3 = createStep(this.getClass(), nonFailMethod, STEP_NODE + "3");

        return new SubstepNode(Lists.newArrayList(stepImpl1, stepImpl2, stepImpl3));
    }

    private StepImplementationNode createStep(Class<?> stepClass, Method stepMethod, String stepLine) {

        final StepImplementationNode stepNode = new StepImplementationNode(stepClass, stepMethod);
        stepNode.getResult().setStarted();
        stepNodes.add(stepNode);
        stepNode.setLine(stepLine);
        stepNode.getResult().setFinished();
        return stepNode;
    }

    @Test
    public void testDetailReport() throws IOException {

        builder.buildReport();

        decomposeReport();

        Assert.assertEquals("The array creation line was not as expected", ARRAY_CREATION_LINE, arrayCreationLine);
        assertThereAreAsManyDetailsInTheReportAsNodesCreated();
        assertRootNodeAsExpected(7 + nodeIdOffset);
        assertFeatureNodeAsExpected(6 + nodeIdOffset);
        assertScenarioNodeAsExpected(5 + nodeIdOffset);
        assertStepNodeAsExpected(3 + nodeIdOffset, STEP_NODE + "3");
        assertStepNodeAsExpected(2 + nodeIdOffset, STEP_NODE + "2");
        assertStepNodeAsExpected(1 + nodeIdOffset, STEP_NODE + "1");
    }

    private void assertRootNodeAsExpected(int index) {

        JsonObject rootNode = details.get(index);

        Assert.assertNotNull(rootNode);

        assertBasics(index, rootNode, "Root node", NOT_RUN);

        JsonArray children = rootNode.getAsJsonArray("children");
        Assert.assertEquals(1, children.size());
        JsonObject child = (JsonObject) Iterables.getOnlyElement(children);
        Assert.assertEquals(NOT_RUN, child.get(RESULT).getAsString());
        Assert.assertEquals(FEATURE_NAME, child.get(DESCRIPTION).getAsString());
    }

    private void assertFeatureNodeAsExpected(int index) {

        JsonObject featureNode = details.get(index);
        assertBasics(index, featureNode, "Feature", NOT_RUN);

        Assert.assertEquals(FEATURE_NAME, featureNode.get(DESCRIPTION).getAsString());

        JsonArray children = featureNode.getAsJsonArray("children");
        Assert.assertEquals(1, children.size());
        JsonObject child = (JsonObject) Iterables.getOnlyElement(children);
        Assert.assertEquals(NOT_RUN, child.get(RESULT).getAsString());
        Assert.assertEquals(SCENARIO_NAME, child.get(DESCRIPTION).getAsString());
    }

    private void assertScenarioNodeAsExpected(int index) {

        JsonObject scenarioNode = details.get(index);
        assertBasics(index, scenarioNode, "Scenario", NOT_RUN);
        Assert.assertEquals(SCENARIO_NAME, scenarioNode.get(DESCRIPTION).getAsString());

        JsonArray children = scenarioNode.getAsJsonArray("children");
        Assert.assertEquals(stepNodes.size(), children.size());

        Iterator<JsonElement> childIterator = children.iterator();

        assertChildStepNode((JsonObject) childIterator.next(), "stepNode1");
        assertChildStepNode((JsonObject) childIterator.next(), "stepNode2");
        assertChildStepNode((JsonObject) childIterator.next(), "stepNode3");
    }

    private void assertChildStepNode(JsonObject child, String description) {

        Assert.assertEquals(PASSED, child.get(RESULT).getAsString());
        Assert.assertEquals(description, child.get(DESCRIPTION).getAsString());
    }

    private void assertStepNodeAsExpected(int index, String description) {

        JsonObject stepNode = details.get(index);
        assertBasics(index, stepNode, "Step", PASSED);
        Assert.assertEquals(description, stepNode.get(DESCRIPTION).getAsString());

        Assert.assertFalse(stepNode.has("children"));
    }

    private void assertBasics(int index, JsonObject node, String nodeType, String result) {

        Assert.assertEquals(nodeType, node.get(NODE_TYPE).getAsString());
        Assert.assertEquals(result, node.get(RESULT).getAsString());
        Assert.assertEquals(index, node.get("id").getAsInt());

    }

    private void assertThereAreAsManyDetailsInTheReportAsNodesCreated() {

        int numberOfNodes = 1 + featureNodes.size() + (scenarioNodes.size() * 2) + stepNodes.size();

        Assert.assertEquals("There should have been a detail line for each node", numberOfNodes, details.size());
    }

    private BufferedReader getDetailReportReader() throws IOException {
        File detailFile = new File(testFolder.getRoot(), DefaultExecutionReportBuilder.FEATURE_REPORT_FOLDER
                + File.separator + DefaultExecutionReportBuilder.JSON_DETAIL_DATA_FILENAME);
        return new BufferedReader(new FileReader(detailFile));
    }

    private void decomposeReport() throws IOException {

        BufferedReader reportReader = null;

        LOG.debug("decomposeReport() entered");

        try {
            reportReader = getDetailReportReader();

            arrayCreationLine = reportReader.readLine();

            Pattern pattern = Pattern.compile(DETAIL_PATTERN);

            JsonParser parser = new JsonParser();
            details = Maps.newHashMap();
            String line;
            while ((line = reportReader.readLine()) != null) {

                LOG.debug("Line found in detail report = " + line);

                Matcher matcher = pattern.matcher(line);

                Assert.assertTrue("A line in the report did not conform to the expected format, line was '" + line
                        + "'", matcher.matches());
                String index = matcher.group(1);
                String detail = matcher.group(2);

                JsonObject json = null;
                try {
                    json = (JsonObject) parser.parse(detail);
                } catch (JsonSyntaxException jse) {
                    jse.printStackTrace();
                    Assert.fail("Invalid json found '" + detail + "'");
                }

                int indexInt = Integer.valueOf(index);

                nodeIdOffset = indexInt < nodeIdOffset ? indexInt - 1 : nodeIdOffset;

                details.put(indexInt, json);
            }
        } finally {
            if (reportReader != null) {
                reportReader.close();

            }
        }

    }

}
