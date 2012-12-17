package com.technophobia.substeps.execution.node;


public interface TestScenarioNodeBuilder<T extends ScenarioNode<?>> {

    T build();
}
