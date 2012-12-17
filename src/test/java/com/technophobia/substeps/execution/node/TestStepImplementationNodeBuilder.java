package com.technophobia.substeps.execution.node;

import java.lang.reflect.Method;


public class TestStepImplementationNodeBuilder implements TestStepNodeBuilder<StepImplementationNode> {

    private StepImplementationNode stepImplementationNode;

    public TestStepImplementationNodeBuilder(Class<?> targetClass, Method targetMethod, int depth, Object... methodArgs) {

        this.stepImplementationNode = new StepImplementationNode(targetClass, targetMethod, depth);
        stepImplementationNode.setMethodArgs(methodArgs);
    }

    public StepImplementationNode build() {

        return stepImplementationNode;
    }
}
