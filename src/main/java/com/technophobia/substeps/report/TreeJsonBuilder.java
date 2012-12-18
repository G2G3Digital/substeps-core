package com.technophobia.substeps.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.NodeWithChildren;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.model.exception.SubstepsRuntimeException;

public class TreeJsonBuilder extends AbstractExecutionNodeVisitor<JsonObject> {

    private final ReportData reportData;

    private static Map<ExecutionResult, String> resultToImageMap = new HashMap<ExecutionResult, String>();

    static {

        resultToImageMap.put(ExecutionResult.PASSED, "img/PASSED.png");
        resultToImageMap.put(ExecutionResult.NOT_RUN, "img/NOT_RUN.png");
        resultToImageMap.put(ExecutionResult.PARSE_FAILURE, "img/PARSE_FAILURE.png");
        resultToImageMap.put(ExecutionResult.FAILED, "img/FAILED.png");
    }

    private final Predicate<ExecutionNode> NODE_HAS_ERROR = new Predicate<ExecutionNode>() {

        public boolean apply(ExecutionNode node) {
            return node.hasError();
        }
    };

    public static void writeTreeJson(ReportData reportData, File jsonFile) {

        new TreeJsonBuilder(reportData).createFile(jsonFile);

    }

    private TreeJsonBuilder(ReportData reportData) {

        this.reportData = reportData;
    }

    private void createFile(File jsonFile) {

        JsonObject tree = buildTree();
        writeFile(jsonFile, tree);
    }

    private void writeFile(File jsonFile, JsonObject tree) {
        BufferedWriter writer = null;

        try {

            writer = Files.newWriter(jsonFile, Charset.defaultCharset());
            writer.append("var treeData = " + tree.toString());

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

    private JsonObject buildTree() {
        List<RootNode> rootNodes = reportData.getRootNodes();

        JsonObject tree = new JsonObject();

        boolean rootNodeInError = Iterables.any(rootNodes, NODE_HAS_ERROR);

        addChildren(tree, rootNodeInError, rootNodes);

        JsonObject data = new JsonObject();
        tree.add("data", data);
        data.addProperty("title", "Substeps tests");

        JsonObject attr = new JsonObject();
        data.add("attr", attr);
        attr.addProperty("id", "0");

        String icon = rootNodeInError ? resultToImageMap.get(ExecutionResult.FAILED) : resultToImageMap
                .get(ExecutionResult.PASSED);
        data.addProperty("icon", icon);

        if (rootNodeInError) {

            data.addProperty("state", "open");
        }
        return tree;
    }

    private JsonObject createJsonWithBasicNodeDetails(IExecutionNode node) {

        JsonObject json = new JsonObject();
        JsonObject data = new JsonObject();
        json.add("data", data);
        data.addProperty("title", getDescriptionForNode(node));
        JsonObject attr = new JsonObject();
        data.add("attr", attr);
        attr.addProperty("id", Long.toString(node.getId()));
        data.addProperty("icon", getNodeImage(node));
        return json;
    }

    @Override
    public JsonObject visit(NodeWithChildren<?> node) {

        return addChildren(createJsonWithBasicNodeDetails(node), node.hasError(), node.getChildren());
    }

    @Override
    public JsonObject visit(StepImplementationNode node) {

        return createJsonWithBasicNodeDetails(node);
    }

    private String getNodeImage(final IExecutionNode node) {
        return resultToImageMap.get(node.getResult().getResult());
    }

    private String getDescriptionForNode(final IExecutionNode node) {

        final StringBuilder buf = new StringBuilder();

        ExecutionReportBuilder.buildDescriptionString(null, node, buf);

        // need to replace "
        String msg = buf.toString();
        if (msg.contains("\"")) {
            msg = msg.replace("\"", "\\\"");
        }

        return msg;
    }

    JsonObject addChildren(JsonObject json, boolean hasError, List<? extends IExecutionNode> childNodes) {

        if (childNodes != null && !childNodes.isEmpty()) {

            if (hasError) {
                json.addProperty("state", "open");
            }

            JsonArray children = new JsonArray();

            for (IExecutionNode node : childNodes) {

                children.add(node.dispatch(this));
            }

            json.add("children", children);

        }

        return json;
    }
}
