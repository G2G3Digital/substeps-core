package com.technophobia.substeps.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.ExecutionNodeVisitor;
import com.technophobia.substeps.model.exception.SubstepsRuntimeException;

public final class DetailedJsonBuilder implements ExecutionNodeVisitor {

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
            
            for(ExecutionNode rootNode : reportData.getRootNodes()) {
                
                rootNode.accept(this);
                
            }
            
        } catch (IOException e) {

            throw new SubstepsRuntimeException("Failed writing to detail json file");
        } finally {
            
            if(writer != null)
            {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    throw new SubstepsRuntimeException("Failed writing to detail json file");
                }
            }
            
        }

    }
    
    public void visit(ExecutionNode executionNode) {

        JsonObject nodeAsJson = createDetailJsonForNode(executionNode);
        try {
            
            writer.append("\ndetail[" + nodeAsJson.get("id") + "]=" + nodeAsJson.toString() + ";");
        
        } catch (IOException e) {

            throw new SubstepsRuntimeException("Failed writing to detail json file");
        }

    }

    public JsonObject createDetailJsonForNode(ExecutionNode node) {
        JsonObject thisNode = new JsonObject();

        thisNode.addProperty("nodetype", node.getType());
        thisNode.addProperty("filename", node.getFilename());
        thisNode.addProperty("result", node.getResult().getResult().toString());
        thisNode.addProperty("id", node.getId());
        thisNode.addProperty("emessage", getExceptionMessage(node));
        thisNode.addProperty("stacktrace", getStackTrace(node));

        thisNode.addProperty("runningDurationMillis", node.getResult().getRunningDuration());
        thisNode.addProperty("runningDurationString", convert(node.getResult().getRunningDuration()));

        addLinkToScreenshot(node.getResult(), thisNode);

        String methodInfo = createMethodInfo(node);

        thisNode.addProperty("method", methodInfo);

        String description = node.getDescription() == null ? null : node.getDescription().trim();
        String descriptionEscaped = replaceNewLines(StringEscapeUtils.escapeHtml4(description));

        thisNode.addProperty("description", descriptionEscaped);

        JsonArray children = new JsonArray();
        if (node.hasChildren()) {
            addDetailsForChildren(node, children);
        }
        thisNode.add("children", children);
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

    private void addDetailsForChildren(ExecutionNode node, JsonArray children) {
        for (ExecutionNode childNode : node.getChildren()) {
            JsonObject childObject = new JsonObject();
            childObject.addProperty("result", childNode.getResult().getResult().toString());
            childObject.addProperty("description", StringEscapeUtils.escapeHtml4(childNode.getDescription()));
            children.add(childObject);
        }
    }

    private String createMethodInfo(ExecutionNode node) {

        final StringBuilder methodInfoBuffer = new StringBuilder();
        node.appendMethodInfo(methodInfoBuffer);

        String methodInfo = methodInfoBuffer.toString();
        if (methodInfo.contains("\"")) {
            methodInfo = methodInfo.replace("\"", "\\\"");
        }

        return replaceNewLines(methodInfo);
    }

    private String getExceptionMessage(ExecutionNode node) {
        String exceptionMessage = "";

        if (node.getResult().getThrown() != null) {

            final String exceptionMsg = StringEscapeUtils.escapeHtml4(node.getResult().getThrown().getMessage());

            exceptionMessage = replaceNewLines(exceptionMsg);

        }

        return exceptionMessage;
    }

    private String getStackTrace(ExecutionNode node) {
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
