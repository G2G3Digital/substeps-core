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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TestBasicScenarioNodeBuilder implements TestScenarioNodeBuilder<BasicScenarioNode> {

    private final String scenarioName;
    private TestSubstepNodeBuilder backgroundBuilder;
    private final List<TestStepNodeBuilder<?>> stepBuilders = Lists.newArrayList();
    private final Set<String> tags = Sets.newHashSet();

    private BasicScenarioNode built;
    private final int depth;

    public TestBasicScenarioNodeBuilder(String scenarioName, int depth) {

        this.scenarioName = scenarioName;
        this.depth = depth;
    }

    public TestSubstepNodeBuilder addBackground() {

        backgroundBuilder = new TestSubstepNodeBuilder(depth + 1);
        return backgroundBuilder;
    }

    public TestSubstepNodeBuilder addSubstep() {

        TestSubstepNodeBuilder testSubstepBuilder = new TestSubstepNodeBuilder(depth + 1);
        this.stepBuilders.add(testSubstepBuilder);
        return testSubstepBuilder;
    }

    public TestBasicScenarioNodeBuilder addStepImpl(Class<?> targetClass, Method targetMethod, Object... methodArgs) {

        TestStepImplementationNodeBuilder testStepImplementationNodeBuilder = new TestStepImplementationNodeBuilder(
                targetClass, targetMethod, depth, methodArgs);
        stepBuilders.add(testStepImplementationNodeBuilder);
        return this;
    }

    public TestBasicScenarioNodeBuilder addStepImpls(int numberOfIdenticalStepsImpls, Class<?> targetClass,
            Method targetMethod) {

        for (int i = 0; i < numberOfIdenticalStepsImpls; i++) {

            addStepImpl(targetClass, targetMethod);
        }
        return this;
    }

    public void addTag(String tag) {

        this.tags.add(tag);
    }

    public void addTags(Set<String> tags) {

        this.tags.addAll(tags);
    }

    public BasicScenarioNode build() {

        SubstepNode backgroundNode = backgroundBuilder != null ? backgroundBuilder.build() : null;

        List<StepNode> stepNodes = Lists.newArrayList();

        for (TestStepNodeBuilder<?> builder : stepBuilders) {

            for (String myTag : tags) {
                builder.addTag(myTag);
            }
            stepNodes.add(builder.build());
        }

        return built = new BasicScenarioNode(scenarioName, backgroundNode, stepNodes, tags, depth);
    }

    public BasicScenarioNode getBuilt() {

        return built;
    }

}
