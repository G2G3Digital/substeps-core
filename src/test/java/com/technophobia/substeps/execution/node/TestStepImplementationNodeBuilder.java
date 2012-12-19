package com.technophobia.substeps.execution.node;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.collect.Sets;

public class TestStepImplementationNodeBuilder implements TestStepNodeBuilder<StepImplementationNode> {

    private final StepImplementationNode stepImplementationNode;

    private final Set<String> tags = Sets.newHashSet();

    public TestStepImplementationNodeBuilder(Class<?> targetClass, Method targetMethod, int depth, Object... methodArgs) {

        this.stepImplementationNode = new StepImplementationNode(targetClass, targetMethod, tags, depth);
        stepImplementationNode.setMethodArgs(methodArgs);
    }

    public TestStepImplementationNodeBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public StepImplementationNode build() {

        return stepImplementationNode;
    }
}
