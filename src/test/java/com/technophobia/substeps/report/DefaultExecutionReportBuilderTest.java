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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.Feature;

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
    private static final String NODE_TYPE = "nodetype";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    ExecutionNode rootNode;

    List<ExecutionNode> featureNodes = Lists.newArrayList();

    List<ExecutionNode> scenarioNodes = Lists.newArrayList();

    List<ExecutionNode> stepNodes = Lists.newArrayList();

    private ReportData reportData;

    private String arrayCreationLine;

    private static final String DETAIL_PATTERN = "detail\\[(\\d+)\\]=(\\{.*\\});";
    private static final String ARRAY_CREATION_LINE = "var detail = new Array();";

    private Map<Integer, JsonObject> details;

    public void nonFailingMethod() {
        System.out.println("no fail");
    }

    public void failingMethod() {
        System.out.println("uh oh");
        throw new IllegalStateException("that's it, had enough");
    }

    @Before
    public void createData() {

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

        reportData = new ReportData();

        rootNode = new ExecutionNode();

        reportData.addRootExecutionNode(rootNode);

        ExecutionNode feature = addFeature(rootNode, FEATURE_NAME, "test file");
        ExecutionNode scenario = addScenario(feature, SCENARIO_NAME);

        addStep(scenario, this.getClass(), nonFailMethod, STEP_NODE + "1");
        addStep(scenario, this.getClass(), failMethod, STEP_NODE + "2");
        addStep(scenario, this.getClass(), nonFailMethod, STEP_NODE + "3");

    }

    private ExecutionNode addFeature(ExecutionNode node, String name, String fileName) {

        final ExecutionNode featureNode = new ExecutionNode();
        final Feature feature = new Feature(name, fileName);
        featureNodes.add(featureNode);
        featureNode.setFeature(feature);
        node.addChild(featureNode);

        return featureNode;
    }

    private ExecutionNode addScenario(ExecutionNode feature, String scenarioName) {

        ExecutionNode scenarioNode = new ExecutionNode();
        scenarioNodes.add(scenarioNode);
        scenarioNode.setScenarioName(scenarioName);
        scenarioNode.setOutline(true);
        feature.addChild(scenarioNode);
        return scenarioNode;
    }

    private void addStep(ExecutionNode scenario, Class<?> stepClass, Method stepMethod, String stepLine) {

        final ExecutionNode stepNode = new ExecutionNode();
        stepNodes.add(stepNode);
        stepNode.setLine(stepLine);
        stepNode.setTargetClass(stepClass);
        stepNode.setTargetMethod(stepMethod);
        scenario.addChild(stepNode);
    }

    @Test
    public void testDetailReport() throws IOException {

        final DefaultExecutionReportBuilder builder = new DefaultExecutionReportBuilder(testFolder.getRoot());

        builder.buildReport(reportData);

        decomposeReport();

        Assert.assertEquals("The array creation line was not as expected", ARRAY_CREATION_LINE, arrayCreationLine);
        assertThereAreAsManyDetailsInTheReportAsNodesCreated();
        assertRootNodeAsExpected(1);
        assertFeatureNodeAsExpected(2);
        assertScenarioNodeAsExpected(3);
        assertStepNodeAsExpected(4, STEP_NODE + "1");
        assertStepNodeAsExpected(5, STEP_NODE + "2");
        assertStepNodeAsExpected(6, STEP_NODE + "3");
    }

    private void assertRootNodeAsExpected(int index) {

        JsonObject rootNode = details.get(index);
        assertBasics(index, rootNode, "Root node", NOT_RUN);

        JsonArray children = rootNode.getAsJsonArray("children");
        Assert.assertEquals(1, children.size());
        JsonObject child = (JsonObject) Iterables.getOnlyElement(children);
        Assert.assertEquals(NOT_RUN, child.get(RESULT).getAsString());
        Assert.assertEquals(FEATURE_NAME, child.get(DESCRIPTION).getAsString());
    }

    private void assertFeatureNodeAsExpected(int index) {

        JsonObject featureNode = details.get(2);
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

        Assert.assertEquals(NOT_RUN, child.get(RESULT).getAsString());
        Assert.assertEquals(description, child.get(DESCRIPTION).getAsString());
    }

    private void assertStepNodeAsExpected(int index, String description) {

        JsonObject stepNode = details.get(index);
        assertBasics(index, stepNode, "Step", NOT_RUN);
        Assert.assertEquals(description, stepNode.get(DESCRIPTION).getAsString());

        JsonArray children = stepNode.getAsJsonArray("children");
        Assert.assertEquals(0, children.size());
    }

    private void assertBasics(int index, JsonObject node, String nodeType, String result) {

        Assert.assertEquals(nodeType, node.get(NODE_TYPE).getAsString());
        Assert.assertEquals(result, node.get(RESULT).getAsString());
        Assert.assertEquals(index, node.get("id").getAsInt());

    }

    private void assertThereAreAsManyDetailsInTheReportAsNodesCreated() {

        int numberOfNodes = 1 + featureNodes.size() + scenarioNodes.size() + stepNodes.size();

        Assert.assertEquals("There should have been a detail line for each node", numberOfNodes, details.size());
    }

    private BufferedReader getDetailReportReader() throws IOException {
        File detailFile = new File(testFolder.getRoot(), DefaultExecutionReportBuilder.FEATURE_REPORT_FOLDER
                + File.separator + DefaultExecutionReportBuilder.JSON_DETAIL_DATA_FILENAME);
        return new BufferedReader(new FileReader(detailFile));
    }

    private void decomposeReport() throws IOException {

        BufferedReader reportReader = null;

        try {
            reportReader = getDetailReportReader();

            arrayCreationLine = reportReader.readLine();

            Pattern pattern = Pattern.compile(DETAIL_PATTERN);

            JsonParser parser = new JsonParser();
            details = Maps.newHashMap();
            String line;
            while ((line = reportReader.readLine()) != null) {

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

                details.put(Integer.valueOf(index), json);
            }
        } finally {
            if (reportReader != null) {
                reportReader.close();

            }
        }

    }

}
