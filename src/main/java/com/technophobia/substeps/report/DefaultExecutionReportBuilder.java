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

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.technophobia.substeps.execution.ExecutionNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * @author ian
 */
public class DefaultExecutionReportBuilder implements ExecutionReportBuilder {
    private final Logger log = LoggerFactory.getLogger(DefaultExecutionReportBuilder.class);

    private final Properties velocityProperties = new Properties();

    /**
     * @parameter default-value = ${project.build.directory}
     */
    private File outputDirectory;


    public DefaultExecutionReportBuilder() {
        velocityProperties.setProperty("resource.loader", "class");
        velocityProperties.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    public DefaultExecutionReportBuilder(File outputDirectory) {
        this();
        this.outputDirectory = outputDirectory;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.report.ExecutionReportBuilder#buildReport(com
     * .technophobia.substeps.report.ReportData, java.io.File)
     */
    public void buildReport(final ReportData data) {

        log.debug("Build report in: " + outputDirectory.getAbsolutePath());

        final File reportDir = new File(outputDirectory + File.separator + "feature_report");

        try {

            log.debug("trying to create: " + reportDir.getAbsolutePath());

            if (reportDir.exists()) {
                FileUtils.deleteDirectory(reportDir);
            }

            Assert.assertTrue("failed to create directory: " + reportDir, reportDir.mkdir());

            copyStaticResources(reportDir);

            buildMainReport(data, reportDir);
            buildTreeJSON(data, reportDir);

//            for (final ExecutionNode node : data.getNodeList()) {
//
//                buildDetailReport(node, reportDir);
//                                   '
//            }
//
//            final ExecutionStats stats = new ExecutionStats();
//            stats.buildStats(data);
//
//            buildSummaryData(stats, reportDir);

        } catch (final IOException ex) {
            log.error("IOException: ", ex);
        } catch (final URISyntaxException ex) {
            log.error("URISyntaxException: ", ex);
        }

        // go through the flattened list and write out any exception stack
        // traces

    }

    private void buildTreeJSON(final ReportData reportData, final File reportDir) throws IOException {
        log.debug("Building tree json file.");

        File jsonFile = new File(reportDir, "tree.json");

        Writer writer = new BufferedWriter(new FileWriter(jsonFile));
        try {
            writer.append("{ \"data\" : \"Feature report 2\", \"state\" : \"open\"");

            List<ExecutionNode> nodeList = reportData.getNodeList();

            if (!nodeList.isEmpty()) {
                writer.append(", \"children\" : [ ");

                boolean first = true;
                for (ExecutionNode node : nodeList) {
                    if(!first) {
                        writer.append(", ");
                    }
                    buildNodeJSON(node, writer);
                    first = false;
                }

                writer.append("]");
            }

            writer.append("}");

        } finally {
            writer.close();
        }


    }

    private void buildNodeJSON(final ExecutionNode node, Writer writer) throws IOException {

        writer.append("{ \"data\" : \"");

        writer.append(getDescriptionForNode(node));

        writer.append("\"");

        if(node.hasChildren()) {
            writer.append(", \"children\" : [");
            boolean first = true;
            for(ExecutionNode child : node.getChildren()) {
                if(!first) {
                    writer.append(", ");
                }
                buildNodeJSON(child, writer);
                first = false;
            }
            writer.append("]");
        }

        writer.append("}");

    }


    /**
     * @param stats
     * @param reportDir
     */
    private void buildSummaryData(final ExecutionStats stats, final File reportDir)
            throws IOException {

        final VelocityContext vCtx = new VelocityContext();

        vCtx.put("stats", stats);

        final String vml = "summary.vm";
        final String targetFilename = "summary.html";

        renderAndWriteToFile(reportDir, vCtx, vml, targetFilename);

        // also create the summary.txt file for reading by sonar (hopefully!)
        final VelocityContext vCtx2 = new VelocityContext();
        vCtx2.put("stats", stats);

        renderAndWriteToFile(reportDir, vCtx2, "summary.txt.vm", "summary.txt");

    }


    /**
     * @param reportDir
     * @throws IOException
     */
    private void copyStaticResources(final File reportDir) throws IOException, URISyntaxException {

        log.debug("Copying static resources to: " + reportDir.getAbsolutePath());

        URL staticURL = getClass().getResource("/static");
        if (staticURL == null) {
            throw new IllegalStateException("Failed to copy static resources for report.  URL for resources is null.");
        }
        FileUtils.copyDirectory(new File(staticURL.toURI()), reportDir);
    }


    /**
     * @param node
     * @param reportDir
     * @throws IOException
     */
    private void buildDetailReport(final ExecutionNode node, final File reportDir)
            throws IOException {

        final VelocityContext vCtx = new VelocityContext();

        vCtx.put("node", node);

        final String vml = "detail.vm";

        renderAndWriteToFile(reportDir, vCtx, vml, node.getId() + "-details.html");
    }

    private static final String EMPTY_IMAGE = "<img src=\"img/empty.gif\" alt=\"\"/>";

    private static final String EXPANDED = "img/minusbottom.gif";
    private static final String LAST_CHILD = "img/joinbottom.gif";
    private static final String CHILD = "img/join.gif";
    private static final String COLLAPSED = "img/plus.gif";


    /**
     * @param node
     * @return
     */
    private String getTreeNodeImage(final ExecutionNode node) {
        String img;
        if (node.hasChildren()) {

            // return + or - depending on depth
            if (node.getDepth() >= 3) {
                img = COLLAPSED;
            } else {
                img = EXPANDED;
            }
        } else {

            // are we last ?
            final List<ExecutionNode> siblings = node.getParent().getChildren();

            if (siblings.indexOf(node) == siblings.size() - 1) {
                img = LAST_CHILD;
            } else {
                img = CHILD;
            }
        }
        return img;
    }


    private String getNodeImage(final ExecutionNode node) {
        return "img/" + node.getResult().getResult() + ".png";
    }


    private void appendMainData(final StringBuilder buf, final ExecutionNode node) {

        final String image = getNodeImage(node);

        final String treeImage = getTreeNodeImage(node);

        buf.append("<a href=\"javascript: o(").append(node.getId()).append(");\"><img id=\"jd")
                .append(node.getId()).append("\" src=\"").append(treeImage)
                .append("\" alt=\"\"/></a>\n<img id=\"id").append(node.getId()).append("\" src=\"")
                .append(image).append("\" alt=\"\"/>\n<a id=\"sd").append(node.getId())
                .append("\" class=\"node\" href=\"").append(node.getId())
                .append("-details.html\" target=\"detailsFrame\" onclick=\"javascript: d.s(")
                .append(node.getId()).append(");\">").append(getDescriptionForNode(node))
                .append("</a>");

    }


    private String getDescriptionForNode(final ExecutionNode node) {
        final StringBuilder buf = new StringBuilder();

        if (node.getParent() == null) {
            // buf.append(0).append(", \"");

            if (node.getLine() != null) {
                buf.append(node.getLine());
            } else {
                buf.append("executionNodeRoot");
            }
        } else {

            buildDescriptionString(null, node, buf);

        }
        return StringEscapeUtils.escapeHtml(buf.toString());
    }


    public static void buildDescriptionString(final String prefix, final ExecutionNode node,
                                              final StringBuilder buf) {
        if (prefix != null) {
            buf.append(prefix);
        }

        if (node.getFeature() != null) {

            buf.append(node.getFeature().getName());

        } else if (node.getScenarioName() != null) {

            if (node.isOutlineScenario()) {
                buf.append("Scenario #: ");
            } else {
                buf.append("Scenario: ");
            }
            buf.append(node.getScenarioName());
        }

        if (node.getParent() != null && node.getParent().isOutlineScenario()) {

            buf.append(node.getRowNumber()).append(" ").append(node.getParent().getScenarioName())
                    .append(":");
        }

        if (node.getLine() != null) {
            buf.append(node.getLine());
        }
    }


    private void buildMainReport(final ReportData data, final File reportDir) throws IOException {

        log.debug("Building main report file.");

        final VelocityContext vCtx = new VelocityContext();

        final String vml = "report_frame.vm";

        final ExecutionStats stats = new ExecutionStats();
        stats.buildStats(data);

        vCtx.put("stats", stats);

        renderAndWriteToFile(reportDir, vCtx, vml, "report_frame.html");

    }


    /**
     * @param reportDir
     * @param vCtx
     * @param vm
     * @param targetFilename
     * @throws IOException
     */
    private void renderAndWriteToFile(final File reportDir, final VelocityContext vCtx,
                                      final String vm, final String targetFilename) throws IOException {

        Writer writer = new BufferedWriter(new FileWriter(new File(reportDir, targetFilename)));

        final VelocityEngine velocityEngine = new VelocityEngine();

        try {

            velocityEngine.init(velocityProperties);
            velocityEngine.getTemplate("templates/" + vm).merge(vCtx, writer);

        } catch (final ResourceNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final ParseErrorException e) {
            throw new RuntimeException(e);
        } catch (final MethodInvocationException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException e) {

                log.error("IOException: ", e);
            }
        }
    }

}
