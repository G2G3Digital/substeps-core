package com.technophobia.substeps.runner.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.ExampleParameter;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.runner.TestParameters;

public class ScenarioNodeBuilder {

    private static final Logger log = LoggerFactory.getLogger(ScenarioNodeBuilder.class);

    private final TestParameters parameters;
    private final SubstepNodeBuilder substepNodeBuilder;

    ScenarioNodeBuilder(TestParameters parameters) {

        this.parameters = parameters;
        this.substepNodeBuilder = new SubstepNodeBuilder(parameters);
    }

    // TODO - to turn off - @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public ScenarioNode<?> build(final Scenario scenario, int depth) {

        if (parameters.isRunnable(scenario)) {

            return buildRunnableScenarioNode(scenario, depth);

        } else {

            log.debug("scenario not runnable: " + scenario.toString());
            return null;
        }
    }

    private ScenarioNode<?> buildRunnableScenarioNode(final Scenario scenario, int depth) {

        ScenarioNode<?> scenarioNode = null;

        try {
            if (scenario.isOutline()) {

                scenarioNode = buildOutlineScenarioNode(scenario, depth);

            } else {

                scenarioNode = buildBasicScenarioNode(scenario, null, depth);
            }
        } catch (final Throwable t) {

            // something has gone wrong parsing this scenario, no point
            // running it so mark it as failed now
            scenarioNode = new BasicScenarioNode(scenario.getDescription(), null, null, depth);
            scenarioNode.getResult().setFailedToParse(t);

            if (parameters.isFailParseErrorsImmediately()) {

                throw new SubstepsConfigurationException(t);
            }
        }

        return scenarioNode;
    }

    public OutlineScenarioNode buildOutlineScenarioNode(final Scenario scenario, int depth) {

        int idx = 0;
        List<OutlineScenarioRowNode> outlineRowNodes = Lists.newArrayListWithExpectedSize(scenario
                .getExampleParameters().size());

        for (final ExampleParameter outlineParameters : scenario.getExampleParameters()) {

            BasicScenarioNode basicSenarioNode = buildBasicScenarioNode(scenario, outlineParameters, depth + 2);
            outlineRowNodes.add(new OutlineScenarioRowNode(idx++, basicSenarioNode, depth + 1));
        }

        return new OutlineScenarioNode(scenario.getDescription(), outlineRowNodes, depth);
    }

    public BasicScenarioNode buildBasicScenarioNode(final Scenario scenario, final ExampleParameter scenarioParameters,
            int depth) {

        SubstepNode background = scenario.hasBackground() ? substepNodeBuilder.build(scenario.getDescription(),
                scenario.getBackground().getSteps(), parameters.getSyntax().getSubStepsMap(), null, scenarioParameters,
                true, depth + 1) : null;

        SubstepNode step = scenario.hasSteps() ? substepNodeBuilder.build(scenario.getDescription(),
                scenario.getSteps(), parameters.getSyntax().getSubStepsMap(), null, scenarioParameters, false,
                depth + 1) : null;

        return new BasicScenarioNode(scenario.getDescription(), background, step, depth);
    }

}
