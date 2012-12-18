package com.technophobia.substeps.execution.node;

import java.util.Set;

public interface TestScenarioNodeBuilder<T extends ScenarioNode<?>> {

    void addTags(Set<String> tags);

    void addTag(String tag);

    T build();
}
