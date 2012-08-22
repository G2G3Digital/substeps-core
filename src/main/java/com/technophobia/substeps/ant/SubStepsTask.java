package com.technophobia.substeps.ant;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.report.ExecutionReportBuilder;
import com.technophobia.substeps.report.ReportData;
import com.technophobia.substeps.runner.BuildFailureManager;
import com.technophobia.substeps.runner.ExecutionConfig;
import com.technophobia.substeps.runner.ExecutionNodeRunner;
import com.technophobia.substeps.runner.SubstepExecutionFailure;

public class SubStepsTask extends Task {
	
	private final Logger log = LoggerFactory.getLogger(SubStepsTask.class);
	private List<AntExecutionConfig> configs = new ArrayList<AntExecutionConfig>();
	private final ExecutionReportBuilder executionReportBuilder = null;


	public void execute() throws BuildException {
		final BuildFailureManager buildFailureManager = new BuildFailureManager();
		
		List<ExecutionConfig> configs = new ArrayList<ExecutionConfig>();
		for (AntExecutionConfig c : this.configs) {
			configs.add((ExecutionConfig)c);
		}
		
		executeInternal(buildFailureManager, configs);
	}

	public void addConfiguredExecutionConfig(AntExecutionConfig config) {
		this.configs.add(config);
	}

	private void executeInternal(final BuildFailureManager buildFailureManager,
			final List<ExecutionConfig> executionConfigList)
			throws RuntimeException {

		final ReportData data = new ReportData();

		Assert.assertNotNull("executionConfigs cannot be null",	executionConfigList);
		Assert.assertFalse("executionConfigs can't be empty", executionConfigList.isEmpty());

		for (final ExecutionConfig executionConfig : executionConfigList) {
			final List<SubstepExecutionFailure> failures = new ArrayList<SubstepExecutionFailure>();
			final ExecutionNode rootNode = runExecutionConfig(executionConfig,
					failures);

			if (executionConfig.getDescription() != null) {
				rootNode.setLine(executionConfig.getDescription());
			}

			data.addRootExecutionNode(rootNode);
			buildFailureManager.sortFailures(failures);
		}

		if (executionReportBuilder != null) {
			executionReportBuilder.buildReport(data);
		}

		if (buildFailureManager.testSuiteFailed()) {
			throw new RuntimeException("Substep Execution failed:\n"
					+ buildFailureManager.getBuildFailureInfo());

		} else if (!buildFailureManager.testSuiteCompletelyPassed()) {
			// print out the failure string (but won't include any failures)
			log.info(buildFailureManager.getBuildFailureInfo());
		}
		// else - we're all good

	}

	private ExecutionNode runExecutionConfig(final ExecutionConfig theConfig,
			final List<SubstepExecutionFailure> failures) {

		final ExecutionNodeRunner runner = new ExecutionNodeRunner();
		final ExecutionNode rootNode = runner.prepareExecutionConfig(theConfig);
		final List<SubstepExecutionFailure> localFailures = runner.run();
		failures.addAll(localFailures);
		return rootNode;
	}
}
