package com.technophobia.substeps.execution.node;


public interface TestStepNodeBuilder<T extends StepNode> {

    T build();
    
}
