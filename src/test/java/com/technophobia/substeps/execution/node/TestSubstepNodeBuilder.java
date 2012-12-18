package com.technophobia.substeps.execution.node;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class TestSubstepNodeBuilder implements TestStepNodeBuilder<SubstepNode> {

    private final List<TestStepNodeBuilder<?>> stepNodeBuilders = Lists.newArrayList();
    private SubstepNode built;
    private final int depth;

    public TestSubstepNodeBuilder(int depth) {

        this.depth = depth;
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

}