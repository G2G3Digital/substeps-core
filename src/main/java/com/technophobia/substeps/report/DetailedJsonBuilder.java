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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.NodeWithChildren;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.model.exception.SubstepsRuntimeException;

public final class DetailedJsonBuilder extends AbstractExecutionNodeVisitor<JsonObject> {

    private final ReportData reportData;
    private final String screenshotFolder;
    private BufferedWriter writer;

    public static void writeDetailJson(ReportData reportData, String screenshotFolder, File jsonFile) {

        new DetailedJsonBuilder(reportData, screenshotFolder).writeFile(jsonFile);

    }

    private DetailedJsonBuilder(ReportData reportData, String screenshotFolder) {

        this.reportData = reportData;
        this.screenshotFolder = screenshotFolder;
    }

    private void writeFile(File jsonFile) {
        try {

            writer = Files.newWriter(jsonFile, Charset.defaultCharset());
            writer.append("var detail = new Array();");

            for (ExecutionNode rootNode : reportData.getRootNodes()) {

                for (JsonObject nodeAsJson : rootNode.accept(this)) {

                    writer.append("\ndetail[" + nodeAsJson.get("id") + "]=" + nodeAsJson.toString() + ";");
                }

            }

        } catch (IOException e) {

            throw new SubstepsRuntimeException("Failed writing to detail json file");
        } finally {

            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    throw new SubstepsRuntimeException("Failed writing to detail json file");
                }
            }

        }

    }

    @Override
    public JsonObject visit(NodeWithChildren<?> node) {

        return createBasicDetailsWithChildDetails(node.getClass().getSimpleName().toString(), node, node.getChildren());
    }

    @Override
    public JsonObject visit(StepImplementationNode stepImplementationNode) {

        JsonObject json = createBasicDetails("Step", stepImplementationNode);
        addLinkToScreenshot(stepImplementationNode.getResult(), json);

        String methodInfo = createMethodInfo(stepImplementationNode);

        json.addProperty("method", methodInfo);

        return json;
    }

    private JsonObject createBasicDetailsWithChildDetails(String nodeType, IExecutionNode node,
            List<? extends IExecutionNode> childNodes) {

        JsonObject json = createBasicDetails(nodeType, node);
        addDetailsForChildren(json, childNodes);

        return json;
    }

    public JsonObject createBasicDetails(String nodeType, IExecutionNode node) {

        JsonObject thisNode = new JsonObject();

        thisNode.addProperty("nodetype", nodeType);
        thisNode.addProperty("filename", node.getFilename());
        thisNode.addProperty("result", node.getResult().getResult().toString());
        thisNode.addProperty("id", node.getId());
        thisNode.addProperty("emessage", getExceptionMessage(node));
        thisNode.addProperty("stacktrace", getStackTrace(node));

        thisNode.addProperty("runningDurationMillis", node.getResult().getRunningDuration());
        thisNode.addProperty("runningDurationString", convert(node.getResult().getRunningDuration()));

        String description = node.getDescription() == null ? null : node.getDescription().trim();
        String descriptionEscaped = replaceNewLines(StringEscapeUtils.escapeHtml4(description));

        thisNode.addProperty("description", descriptionEscaped);

        return thisNode;
    }

    private void addLinkToScreenshot(ExecutionNodeResult result, JsonObject thisNode) {

        if (result.getScreenshot() != null) {
            thisNode.addProperty("screenshot", screenshotFolder + File.separator + result.getExecutionNodeId()
                    + ScreenshotWriter.SCREENSHOT_SUFFIX);
        }
    }

    private String convert(Long runningDurationMillis) {

        return runningDurationMillis == null ? "No duration recorded" : convert(runningDurationMillis.longValue());
    }

    private String convert(long runningDurationMillis) {
        Duration duration = new Duration(runningDurationMillis);
        PeriodFormatter formatter = PeriodFormat.getDefault();
        return formatter.print(duration.toPeriod());
    }

    private void addDetailsForChildren(JsonObject json, List<? extends IExecutionNode> childNodes) {

        JsonArray children = new JsonArray();
        json.add("children", children);

        for (IExecutionNode childNode : childNodes) {

            JsonObject childObject = new JsonObject();
            childObject.addProperty("result", childNode.getResult().getResult().toString());
            childObject.addProperty("description", StringEscapeUtils.escapeHtml4(childNode.getDescription()));
            children.add(childObject);
        }
    }

    private String createMethodInfo(StepImplementationNode node) {

        final StringBuilder methodInfoBuffer = new StringBuilder();
        node.appendMethodInfo(methodInfoBuffer);

        String methodInfo = methodInfoBuffer.toString();
        if (methodInfo.contains("\"")) {
            methodInfo = methodInfo.replace("\"", "\\\"");
        }

        return replaceNewLines(methodInfo);
    }

    private String getExceptionMessage(IExecutionNode node) {
        String exceptionMessage = "";

        if (node.getResult().getThrown() != null) {

            final String exceptionMsg = StringEscapeUtils.escapeHtml4(node.getResult().getThrown().getMessage());

            exceptionMessage = replaceNewLines(exceptionMsg);

        }

        return exceptionMessage;
    }

    private String getStackTrace(IExecutionNode node) {
        String stackTrace = "";

        if (node.getResult().getThrown() != null) {

            final StackTraceElement[] stackTraceElements = node.getResult().getThrown().getStackTrace();

            final StringBuilder buf = new StringBuilder();

            for (final StackTraceElement e : stackTraceElements) {

                buf.append(StringEscapeUtils.escapeHtml4(e.toString().trim())).append("<br/>");
            }
            stackTrace = buf.toString();
        }

        return stackTrace;
    }

    private String replaceNewLines(final String s) {

        if (s != null && s.contains("\n")) {

            return s.replaceAll("\n", "<br/>");
        } else {
            return s;
        }
    }

}
