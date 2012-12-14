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
    
    private TestParameters parameters;
    private SubstepNodeBuilder substepNodeBuilder;

    ScenarioNodeBuilder(TestParameters parameters) {

        this.parameters = parameters;
        this.substepNodeBuilder = new SubstepNodeBuilder(parameters);
    }

    // TODO - to turn off - @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public ScenarioNode build(final Scenario scenario) {

        if (parameters.isRunnable(scenario)) {

            return buildRunnableScenarioNode(scenario);

        } else {

            log.debug("scenario not runnable: " + scenario.toString());
            return null;
        }
    }

    private ScenarioNode buildRunnableScenarioNode(final Scenario scenario) {

        ScenarioNode scenarioNode = null;

        try {
            if (scenario.isOutline()) {

                scenarioNode = buildOutlineScenarioNode(scenario);

            } else {

                scenarioNode = buildBasicScenarioNode(scenario, null);
            }
        } catch (final Throwable t) {

            // something has gone wrong parsing this scenario, no point
            // running it so mark it as failed now
            scenarioNode = new BasicScenarioNode(scenario.getDescription(), null, null);
            scenarioNode.getResult().setFailedToParse(t);

            if (parameters.isFailParseErrorsImmediately()) {

                throw new SubstepsConfigurationException(t);
            }
        }

        return scenarioNode;
    }

    public OutlineScenarioNode buildOutlineScenarioNode(final Scenario scenario) {

        int idx = 0;
        List<OutlineScenarioRowNode> outlineRowNodes = Lists.newArrayListWithExpectedSize(scenario
                .getExampleParameters().size());

        for (final ExampleParameter outlineParameters : scenario.getExampleParameters()) {

            BasicScenarioNode basicSenarioNode = buildBasicScenarioNode(scenario, outlineParameters);
            outlineRowNodes.add(new OutlineScenarioRowNode(idx++, basicSenarioNode));
        }

        return new OutlineScenarioNode(scenario.getDescription(), outlineRowNodes);
    }

    public BasicScenarioNode buildBasicScenarioNode(final Scenario scenario, final ExampleParameter scenarioParameters) {

        SubstepNode background = scenario.hasBackground() ? substepNodeBuilder.build(scenario.getBackground().getSteps(),
                parameters.getSyntax().getSubStepsMap(), null, scenarioParameters, true) : null;

        SubstepNode step = scenario.hasSteps() ? substepNodeBuilder.build(scenario.getSteps(), parameters.getSyntax()
                .getSubStepsMap(), null, scenarioParameters, false) : null;

        return new BasicScenarioNode(scenario.getDescription(), background, step);
    }

}
