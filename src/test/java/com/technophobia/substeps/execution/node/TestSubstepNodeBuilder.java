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