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
package com.technophobia.substeps.execution.node;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.technophobia.substeps.execution.Feature;

public class TestFeatureNodeBuilder {

    private final Feature feature;

    private FeatureNode built;

    private final Set<String> tags = Sets.newHashSet();

    List<TestScenarioNodeBuilder<?>> scenarioBuilders = Lists.newArrayList();

    public TestFeatureNodeBuilder(Feature feature) {
        this.feature = feature;
    }

    public TestBasicScenarioNodeBuilder addBasicScenario(String scenarioName) {

        TestBasicScenarioNodeBuilder testBasicScenarioNodeBuilder = new TestBasicScenarioNodeBuilder(scenarioName, 2);
        scenarioBuilders.add(testBasicScenarioNodeBuilder);
        return testBasicScenarioNodeBuilder;
    }

    public TestOutlineScenarioNodeBuilder addOutlineScenario(String scenarioName) {

        TestOutlineScenarioNodeBuilder outlineBuilder = new TestOutlineScenarioNodeBuilder(scenarioName, 2);
        scenarioBuilders.add(outlineBuilder);
        return outlineBuilder;
    }

    public FeatureNode build() {

        List<ScenarioNode<?>> scenarioNodes = Lists.newArrayListWithCapacity(scenarioBuilders.size());
        for (TestScenarioNodeBuilder<?> builder : scenarioBuilders) {

            builder.addTags(this.tags);
            scenarioNodes.add(builder.build());
        }
        built = new FeatureNode(feature, scenarioNodes, tags);
        return built;
    }

    public FeatureNode getBuilt() {

        return built;
    }

    public TestFeatureNodeBuilder addTags(String... tags) {

        this.tags.addAll(Arrays.asList(tags));
        return this;
    }
}
