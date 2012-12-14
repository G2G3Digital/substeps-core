package com.technophobia.substeps.runner.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.ExampleParameter;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.SubSteps.StepParameter;
import com.technophobia.substeps.model.Util;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.model.parameter.Converter;
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

        List<ScenarioNode> scenarioNodes = Lists.newArrayListWithExpectedSize(featureFile.getScenarios().size());

        for (final Scenario scenario : featureFile.getScenarios()) {

            if (scenario != null) {

                ScenarioNode scenarioNode = scenarioNodeBuilder.build(scenario);
                if (scenarioNode != null) {

                    scenarioNodes.add(scenarioNode);
                }
            }
        }

        final Feature feature = new Feature(featureFile.getName(), featureFile.getSourceFile().getName());

        final FeatureNode featureNode = new FeatureNode(feature, scenarioNodes);

        featureNode.setFileUri(featureFile.getSourceFile().getAbsolutePath());
        featureNode.setLineNumber(0);
        featureNode.setTags(featureFile.getTags());

        return featureNode;
    }

}
