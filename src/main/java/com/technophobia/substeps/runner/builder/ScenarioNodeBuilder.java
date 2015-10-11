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
package com.technophobia.substeps.runner.builder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.runner.SubstepExecutionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.ExampleParameter;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
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
    public ScenarioNode<?> build(final Scenario scenario, final Set<String> inheritedTags, int depth) {

        if (parameters.isRunnable(scenario)) {

            return buildRunnableScenarioNode(scenario, inheritedTags, depth);

        } else {

            log.debug("scenario not runnable: " + scenario.toString());
            return null;
        }
    }

    private ScenarioNode<?> buildRunnableScenarioNode(final Scenario scenario, Set<String> inheritedTags, int depth) {

        ScenarioNode<?> scenarioNode = null;

        try {
            if (scenario.isOutline()) {

                scenarioNode = buildOutlineScenarioNode(scenario, inheritedTags, depth);

            } else {

                scenarioNode = buildBasicScenarioNode(scenario, null, inheritedTags, depth);
            }
        } catch (final Throwable t) {

            // something has gone wrong parsing this scenario, no point
            // running it so mark it as failed now
            if (log.isDebugEnabled()) {
                log.debug("Failed to parse " + scenario.getDescription() + ", creating dummy node", t);
            }
            scenarioNode = new BasicScenarioNode(scenario.getDescription(), null, Collections.<StepNode> emptyList(),
                    Collections.<String> emptySet(), depth);

            new SubstepExecutionFailure(t, scenarioNode, ExecutionResult.PARSE_FAILURE);
//            scenarioNode.getResult().setFailedToParse(t);

            if (parameters.isFailParseErrorsImmediately()) {

                throw new SubstepsConfigurationException(t);
            }
        }

        return scenarioNode;
    }

    public OutlineScenarioNode buildOutlineScenarioNode(final Scenario scenario, Set<String> inheritedTags, int depth) {

        int idx = 0;
        List<OutlineScenarioRowNode> outlineRowNodes = Lists.newArrayListWithExpectedSize(scenario
                .getExampleParameters().size());

        Set<String> allTags = Sets.newHashSet();
        allTags.addAll(inheritedTags);

        if (scenario.getTags() != null) {
            allTags.addAll(scenario.getTags());
        }

        for (final ExampleParameter outlineParameters : scenario.getExampleParameters()) {

            BasicScenarioNode basicSenarioNode = buildBasicScenarioNode(scenario, outlineParameters, allTags, depth + 2);
            outlineRowNodes.add(new OutlineScenarioRowNode(idx++, basicSenarioNode, allTags, depth + 1));
        }

        return new OutlineScenarioNode(scenario.getDescription(), outlineRowNodes, allTags, depth);
    }

    public BasicScenarioNode buildBasicScenarioNode(final Scenario scenario, final ExampleParameter scenarioParameters,
            Set<String> inheritedTags, int depth) {

        Set<String> allTags = Sets.newHashSet();
        allTags.addAll(inheritedTags);

        if (scenario.getTags() != null) {

            allTags.addAll(scenario.getTags());
        }

        SubstepNode background = scenario.hasBackground() ? substepNodeBuilder.build(scenario.getDescription(),
                scenario.getBackground().getSteps(), parameters.getSyntax().getSubStepsMap(), null, scenarioParameters,
                true, allTags, depth + 1) : null;

        List<StepNode> steps = Lists.newArrayList();

        if (scenario.hasSteps()) {

            for (Step step : scenario.getSteps()) {

                steps.add(substepNodeBuilder.buildStepNode(scenario.getDescription(), step, parameters.getSyntax()
                        .getSubStepsMap(), null, scenarioParameters, false, allTags, depth + 1));
            }

        }

        return new BasicScenarioNode(scenario.getDescription(), background, steps, allTags, depth);
    }

}
