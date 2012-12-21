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

    private static final int _2 = 2;

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

                ScenarioNode<?> scenarioNode = scenarioNodeBuilder.build(scenario, tags, _2);
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
