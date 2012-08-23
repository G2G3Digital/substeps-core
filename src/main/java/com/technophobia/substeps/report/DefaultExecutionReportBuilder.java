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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

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

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.technophobia.substeps.execution.ExecutionNode;

/**
 * @author ian
 * 
 */
public class DefaultExecutionReportBuilder implements ExecutionReportBuilder {
	private final Logger log = LoggerFactory
			.getLogger(DefaultExecutionReportBuilder.class);

	private final Properties velocityProperties = new Properties();

	/**
	 * @parameter default-value = ${project.build.directory}
	 */
	private File outputDirectory;

	public DefaultExecutionReportBuilder() {
		velocityProperties.setProperty("resource.loader", "class");
		velocityProperties
				.setProperty("class.resource.loader.class",
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

		final File reportDir = new File(outputDirectory + File.separator
				+ "feature_report");

		try {

			log.debug("trying to create: " + reportDir.getAbsolutePath());

			if (reportDir.exists()) {
				FileUtils.deleteDirectory(reportDir);
			}

			Assert.assertTrue("failed to create directory: " + reportDir,
					reportDir.mkdir());

			copyStaticResources(reportDir);

			buildMainReport(data, reportDir);

			for (final ExecutionNode node : data.getNodeList()) {

				buildDetailReport(node, reportDir);

			}

			final ExecutionStats stats = new ExecutionStats();
			stats.buildStats(data);

			buildSummaryData(stats, reportDir);

		} catch (final IOException e) {

			log.error("IOException: ", e);
		}

		// go through the flattened list and write out any exception stack
		// traces

	}

	/**
	 * @param stats
	 * @param reportDir
	 */
	private void buildSummaryData(final ExecutionStats stats,
			final File reportDir) throws IOException {

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
	private void copyStaticResources(final File reportDir) throws IOException {
		copyStaticResource(reportDir, "report_frame.html", "");

		copyStaticResource(reportDir, "dtree.css", "");
		copyStaticResource(reportDir, "dtree.js", "");

		final File imgDir = new File(reportDir + File.separator + "img");
		Assert.assertTrue("failed to create directory: " + imgDir,
				imgDir.mkdir());

		for (final String img : STATIC_IMAGES) {
			copyStaticResource(imgDir, img, "img/");
		}

	}

	private static final String[] STATIC_IMAGES = { "base.gif", "FAILED.png",
			"globe.gif", "join.gif", "minus.gif", "nolines_plus.gif",
			"PASSED.png", "question.gif", "cd.gif", "folder.gif",
			"imgfolder.gif", "line.gif", "musicfolder.gif", "NOT_RUN.png",
			"plusbottom.gif", "trash.gif", "empty.gif", "folderopen.gif",
			"joinbottom.gif", "minusbottom.gif", "nolines_minus.gif",
			"page.gif", "plus.gif", "NON_CRITICAL_FAILURE.png",
			"PARSE_FAILURE.png" };

	/**
	 * @param reportDir
	 * @throws IOException
	 */
	private void copyStaticResource(final File reportDir,
			final String resource, final String subfolder) throws IOException {

		log.debug("copyStaticResource: reportDir: "
				+ reportDir.getAbsolutePath() + " resource: " + resource
				+ " subfolder: " + subfolder);

		InputStream resourceAsStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("static/" + subfolder + resource);

		if (resourceAsStream == null) {
			resourceAsStream = this.getClass().getClassLoader()
					.getResourceAsStream("static/" + subfolder + resource);
		}

		final File newOutput = new File(reportDir, resource);

		Assert.assertTrue("failed to create new file",
				newOutput.createNewFile());

		Files.copy(new FileInputSupplier(resourceAsStream), newOutput);
	}

	private static class FileInputSupplier implements
			InputSupplier<InputStream> {
		private final InputStream is;

		public FileInputSupplier(final InputStream is) {
			this.is = is;
		}

		public InputStream getInput() {
			return is;
		}
	}

	/**
	 * @param node
	 * @param reportDir
	 * @throws IOException
	 */
	private void buildDetailReport(final ExecutionNode node,
			final File reportDir) throws IOException {

		final VelocityContext vCtx = new VelocityContext();

		vCtx.put("node", node);

		final String vml = "detail.vm";

		renderAndWriteToFile(reportDir, vCtx, vml, node.getId()
				+ "-details.html");
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

	private void appendMainData(final StringBuilder buf,
			final ExecutionNode node) {

		final String image = getNodeImage(node);

		final String treeImage = getTreeNodeImage(node);

		buf.append("<a href=\"javascript: o(")
				.append(node.getId())
				.append(");\"><img id=\"jd")
				.append(node.getId())
				.append("\" src=\"")
				.append(treeImage)
				.append("\" alt=\"\"/></a>\n<img id=\"id")
				.append(node.getId())
				.append("\" src=\"")
				.append(image)
				.append("\" alt=\"\"/>\n<a id=\"sd")
				.append(node.getId())
				.append("\" class=\"node\" href=\"")
				.append(node.getId())
				.append("-details.html\" target=\"detailsFrame\" onclick=\"javascript: d.s(")
				.append(node.getId()).append(");\">")
				.append(getDescriptionForNode(node)).append("</a>");

	}

	private String getDescriptionForNode(final ExecutionNode node) {
		final StringBuilder buf = new StringBuilder();

		if (node.getParent() == null) {
			// buf.append(0).append(", \"");

			if (node.getLine() != null) {
				buf.append(node.getLine());
			} else {
				buf.append("executionNodeRoot\"");
			}
		} else {

			buildDescriptionString(null, node, buf);

		}
		return StringEscapeUtils.escapeHtml(buf.toString());
	}

	public static void buildDescriptionString(final String prefix,
			final ExecutionNode node, final StringBuilder buf) {
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

			buf.append(node.getRowNumber()).append(" ")
					.append(node.getParent().getScenarioName()).append(":");
		}

		if (node.getLine() != null) {
			buf.append(node.getLine());
		}
	}

	private void appendTreeNode(final StringBuilder buf,
			final ExecutionNode node) {

		buf.append("<div class=\"dTreeNode\">");

		buf.append(Strings.repeat(EMPTY_IMAGE, node.getDepth()));

		appendMainData(buf, node);

		buf.append("</div>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technophobia.substeps.report.ExecutionReportBuilder#buildTreeString
	 * (java.lang.StringBuilder,
	 * com.technophobia.substeps.execution.ExecutionNode,
	 * com.technophobia.substeps.report.ReportData)
	 */
	public void buildTreeString(final StringBuilder buf,
			final ExecutionNode node, final ReportData data) {

		String display = getDisplay(node.getDepth());

		if (node.getParent() == null && node.hasChildren()) {
			parentDivStart(node.getId() - 1, buf, display);
		}

		appendTreeNode(buf, node);

		if (node.hasChildren()) {

			display = getDisplay(node.getDepth() + 1);
			parentDivStart(node.getId(), buf, display);

			for (final ExecutionNode child : node.getChildren()) {
				buf.append("<!-- child id " + child.getId() + " -->");

				buildTreeString(buf, child, data);

				buf.append("<!-- end child id " + child.getId() + " -->");
			}

			buf.append("</div>");
		}
	}

	/**
	 * @param depth
	 * @return
	 */
	private String getDisplay(final int depth) {
		String display = "block";
		// TODO make this a parameter
		if (depth >= 4) {
			display = "none";
		}
		return display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technophobia.substeps.report.ExecutionReportBuilder#parentDivStart
	 * (long, java.lang.StringBuilder, java.lang.String)
	 */
	public void parentDivStart(final long id, final StringBuilder buf,
			final String display) {
		buf.append("<div id=\"dd").append(id)
				.append("\" class=\"clip\" style=\"display: ").append(display)
				.append(";\">");
	}

	private void buildMainReport(final ReportData data, final File reportDir)
			throws IOException {

		final VelocityContext vCtx = new VelocityContext();

		final String vml = "report2.vm";

		final StringBuilder buf = new StringBuilder();

		for (final ExecutionNode rootNode : data.getRootNodes()) {

			buildTreeString(buf, rootNode, data);
		}

		vCtx.put("tree", buf.toString());

		final String targetFilename = "tree.html";

		renderAndWriteToFile(reportDir, vCtx, vml, targetFilename);

	}

	/**
	 * @param reportDir
	 * @param vCtx
	 * @param vml
	 * @param targetFilename
	 * @throws IOException
	 */
	private void renderAndWriteToFile(final File reportDir,
			final VelocityContext vCtx, final String vml,
			final String targetFilename) throws IOException {
		final String rendered = renderText(vml, vCtx);

		writeToFile(reportDir, targetFilename, rendered);
	}

	/**
	 * @param reportDir
	 * @param targetFilename
	 * @param rendered
	 * @throws IOException
	 */
	private void writeToFile(final File reportDir, final String targetFilename,
			final String rendered) throws IOException {
		final File mainreport = new File(reportDir, targetFilename);

		Assert.assertTrue("failed to create new file",
				mainreport.createNewFile());

		Files.write(rendered, mainreport, Charset.defaultCharset());
	}

	private String renderText(final String vm, final VelocityContext vCtx) {
		String rendered = null;

		StringWriter writer = null;
		try {
			final VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.init(velocityProperties);

			writer = new StringWriter();

			velocityEngine.getTemplate("templates/" + vm).merge(vCtx, writer);

			rendered = writer.getBuffer().toString();

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
		return rendered;
	}
}
