package com.technophobia.substeps.runner.builder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.runner.TestParameters;

public class FeatureNodeBuilder {

    private static final Logger log = LoggerFactory.getLogger(FeatureNodeBuilder.class);

    private final TestParameters parameters;
    private final ScenarioNodeBuilder scenarioNodeBuilder;

    FeatureNodeBuilder(TestParameters parameters) {

        this.parameters = parameters;
        this.scenarioNodeBuilder = new ScenarioNodeBuilder(parameters);
    }

    public FeatureNode build(final FeatureFile featureFile) {

        if (parameters.isRunnable(featureFile)) {

            return buildRunnableFeatureNode(featureFile);

        } else {
            log.debug("feature not runnable: " + featureFile.toString());
            return null;
        }

    }

    private FeatureNode buildRunnableFeatureNode(FeatureFile featureFile) {

        List<ScenarioNode<?>> scenarioNodes = Lists.newArrayListWithExpectedSize(featureFile.getScenarios().size());

        Set<String> tags = featureFile.getTags() != null ? featureFile.getTags() : Collections.<String> emptySet();

        for (final Scenario scenario : featureFile.getScenarios()) {

            if (scenario != null) {

                ScenarioNode<?> scenarioNode = scenarioNodeBuilder.build(scenario, tags, 2);
                if (scenarioNode != null) {

                    scenarioNodes.add(scenarioNode);
                }
            }
        }

        final Feature feature = new Feature(featureFile.getName(), featureFile.getSourceFile().getName());

        final FeatureNode featureNode = new FeatureNode(feature, scenarioNodes, tags);

        featureNode.setFileUri(featureFile.getSourceFile().getAbsolutePath());
        featureNode.setLineNumber(0);

        return featureNode;
    }

}
