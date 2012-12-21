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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TestSubstepNodeBuilder implements TestStepNodeBuilder<SubstepNode> {

    private final List<TestStepNodeBuilder<?>> stepNodeBuilders = Lists.newArrayList();
    private SubstepNode built;
    private final int depth;

    private final Set<String> tags = Sets.newHashSet();

    public TestSubstepNodeBuilder(int depth) {

        this.depth = depth;
    }

    public TestSubstepNodeBuilder addSubstep() {

        TestSubstepNodeBuilder testSubstepBuilder = new TestSubstepNodeBuilder(depth + 1);
        stepNodeBuilders.add(testSubstepBuilder);
        return testSubstepBuilder;
    }

    public TestSubstepNodeBuilder addStepImpl(Class<?> targetClass, Method targetMethod, Object... methodArgs) {

        TestStepImplementationNodeBuilder testStepImplementationNodeBuilder = new TestStepImplementationNodeBuilder(
                targetClass, targetMethod, depth, methodArgs);
        stepNodeBuilders.add(testStepImplementationNodeBuilder);
        return this;
    }

    public SubstepNode build() {
        List<StepNode> stepNodes = Lists.newArrayListWithCapacity(stepNodeBuilders.size());
        for (TestStepNodeBuilder<?> builder : stepNodeBuilders) {

            for (String myTag : tags) {

                builder.addTag(myTag);
            }

            stepNodes.add(builder.build());
        }
        return built = new SubstepNode(stepNodes, Collections.<String> emptySet(), depth);
    }

    public TestSubstepNodeBuilder addStepImpls(int numberOfIdenticalStepsImpls, Class<?> targetClass,
            Method targetMethod) {

        for (int i = 0; i < numberOfIdenticalStepsImpls; i++) {

            addStepImpl(targetClass, targetMethod);
        }
        return this;
    }

    public SubstepNode getBuilt() {

        return built;
    }

    public TestSubstepNodeBuilder addTag(String tag) {

        this.tags.add(tag);
        return this;
    }
}