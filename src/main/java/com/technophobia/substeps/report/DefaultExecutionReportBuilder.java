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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.protocol.file.FileURLConnection;

import com.google.common.io.Files;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.RootNode;

/**
 * @author ian
 */
public class DefaultExecutionReportBuilder extends ExecutionReportBuilder {

    private final Logger log = LoggerFactory.getLogger(DefaultExecutionReportBuilder.class);

    private final Properties velocityProperties = new Properties();

    public static final String FEATURE_REPORT_FOLDER = "feature_report";
    private static final String SCREENSHOT_FOLDER = "screenshots";
    public static final String JSON_DATA_FILENAME = "report_data.json";
    public static final String JSON_DETAIL_DATA_FILENAME = "detail_data.js";

    private static final String JSON_STATS_DATA_FILENAME = "susbteps-stats.js";

    private static Map<ExecutionResult, String> resultToImageMap = new HashMap<ExecutionResult, String>();

    private ReportData data = new ReportData();

    static {

        resultToImageMap.put(ExecutionResult.PASSED, "img/PASSED.png");
        resultToImageMap.put(ExecutionResult.NOT_RUN, "img/NOT_RUN.png");
        resultToImageMap.put(ExecutionResult.PARSE_FAILURE, "img/PARSE_FAILURE.png");
        resultToImageMap.put(ExecutionResult.FAILED, "img/FAILED.png");
    }

    /**
     * @parameter default-value = ${project.build.directory}
     */
    private File outputDirectory;

    /**
     * @parameter default-value = "Substeps report"
     */
    private String reportTitle;

    public DefaultExecutionReportBuilder() {
        velocityProperties.setProperty("resource.loader", "class");
        velocityProperties.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    @Override
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void buildReport() {

        log.debug("Build report in: " + outputDirectory.getAbsolutePath());

        final File reportDir = new File(outputDirectory + File.separator + FEATURE_REPORT_FOLDER);

        File screenshotDirectory = new File(reportDir, SCREENSHOT_FOLDER);

        try {

            log.debug("trying to create: " + reportDir.getAbsolutePath());

            if (reportDir.exists()) {
                FileUtils.deleteDirectory(reportDir);
            }

            Assert.assertTrue("failed to create directory: " + reportDir, reportDir.mkdirs());

            copyStaticResources(reportDir);

            buildMainReport(data, reportDir);

            TreeJsonBuilder.writeTreeJson(data, new File(reportDir, JSON_DATA_FILENAME));

            DetailedJsonBuilder
                    .writeDetailJson(data, SCREENSHOT_FOLDER, new File(reportDir, JSON_DETAIL_DATA_FILENAME));

            buildStatsJSON(data, reportDir);

            for (ExecutionNode rootNode : data.getRootNodes()) {

                ScreenshotWriter.writeScreenshots(screenshotDirectory, rootNode);
            }

        } catch (final IOException ex) {
            log.error("IOException: ", ex);
        } catch (final URISyntaxException ex) {
            log.error("URISyntaxException: ", ex);
        }
    }

    /**
     * @param data
     * @param reportDir
     */
    private void buildStatsJSON(final ReportData data, final File reportDir) throws IOException {

        final File jsonFile = new File(reportDir, JSON_STATS_DATA_FILENAME);

        final ExecutionStats stats = new ExecutionStats();
        stats.buildStats(data);

        final BufferedWriter writer = Files.newWriter(jsonFile, Charset.defaultCharset());
        try {
            buildStatsJSON(stats, writer);
        } finally {
            writer.close();
        }

    }

    /**
     * @param stats
     * @param writer
     */
    private void buildStatsJSON(final ExecutionStats stats, final BufferedWriter writer) throws IOException {

        writer.append("var featureStatsData = [");
        boolean first = true;

        for (final TestCounterSet stat : stats.getSortedList()) {

            if (!first) {
                writer.append(",\n");
            }
            writer.append("[\"").append(stat.getTag()).append("\",");
            writer.append("\"").append(Integer.toString(stat.getFeatureStats().getCount())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getFeatureStats().getRun())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getFeatureStats().getPassed())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getFeatureStats().getFailed())).append("\",");
            writer.append("\"").append(Double.toString(stat.getFeatureStats().getSuccessPc())).append("\"]");

            first = false;
        }

        writer.append("];\n");

        writer.append("var scenarioStatsData = [");
        first = true;

        for (final TestCounterSet stat : stats.getSortedList()) {

            if (!first) {
                writer.append(",\n");
            }
            writer.append("[\"").append(stat.getTag()).append("\",");
            writer.append("\"").append(Integer.toString(stat.getScenarioStats().getCount())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getScenarioStats().getRun())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getScenarioStats().getPassed())).append("\",");
            writer.append("\"").append(Integer.toString(stat.getScenarioStats().getFailed())).append("\",");
            writer.append("\"").append(Double.toString(stat.getScenarioStats().getSuccessPc())).append("\"")
                    .append("]");

            first = false;
        }

        writer.append("];\n");

    }

    /**
     * @param reportDir
     * @throws IOException
     */
    private void copyStaticResources(final File reportDir) throws URISyntaxException, IOException {

        log.debug("Copying static resources to: " + reportDir.getAbsolutePath());

        final URL staticURL = getClass().getResource("/static");
        if (staticURL == null) {
            throw new IllegalStateException("Failed to copy static resources for report.  URL for resources is null.");
        }

        copyResourcesRecursively(staticURL, reportDir);
    }

    private void buildMainReport(final ReportData data, final File reportDir) throws IOException {

        log.debug("Building main report file.");

        final VelocityContext vCtx = new VelocityContext();

        final String vml = "report_frame.vm";

        final ExecutionStats stats = new ExecutionStats();
        stats.buildStats(data);

        final SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        final String dateTimeStr = sdf.format(new Date());

        vCtx.put("stats", stats);
        vCtx.put("dateTimeStr", dateTimeStr);
        vCtx.put("reportTitle", reportTitle);

        renderAndWriteToFile(reportDir, vCtx, vml, "report_frame.html");

    }

    private void renderAndWriteToFile(final File reportDir, final VelocityContext vCtx, final String vm,
            final String targetFilename) throws IOException {

        final Writer writer = new BufferedWriter(new FileWriter(new File(reportDir, targetFilename)));

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

    public void copyResourcesRecursively(final URL originUrl, final File destination) throws IOException {
        final URLConnection urlConnection = originUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
        } else if (urlConnection instanceof FileURLConnection) {
            FileUtils.copyDirectory(new File(originUrl.getPath()), destination);
        } else {
            throw new RuntimeException("URLConnection[" + urlConnection.getClass().getSimpleName()
                    + "] is not a recognized/implemented connection type.");
        }
    }

    public void copyJarResourcesRecursively(final File destination, final JarURLConnection jarConnection)
            throws IOException {
        final JarFile jarFile = jarConnection.getJarFile();
        for (final JarEntry entry : Collections.list(jarFile.entries())) {
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                final String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
                if (!entry.isDirectory()) {
                    InputStream entryInputStream = null;
                    try {
                        entryInputStream = jarFile.getInputStream(entry);
                        FileUtils.copyInputStreamToFile(entryInputStream, new File(destination, fileName));
                    } finally {
                        IOUtils.closeQuietly(entryInputStream);
                    }
                } else {
                    new File(destination, fileName).mkdirs();
                }
            }
        }
    }

    @Override
    public void addRootExecutionNode(RootNode node) {

        data.addRootExecutionNode(node);
    }

}
