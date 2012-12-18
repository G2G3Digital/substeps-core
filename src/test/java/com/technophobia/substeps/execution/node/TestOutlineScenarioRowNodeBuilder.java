package com.technophobia.substeps.execution.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class TestOutlineScenarioRowNodeBuilder {

    private TestBasicScenarioNodeBuilder basicScenarioBuilder;

    private final int rowIndex;
    private final int depth;
    private final Set<String> tags = Sets.newHashSet();

    private OutlineScenarioRowNode built;

    public TestOutlineScenarioRowNodeBuilder(int rowIndex, int depth) {

        this.rowIndex = rowIndex;
        this.depth = depth;
    }

    public TestBasicScenarioNodeBuilder setBasicScenario(String scenarioName) {

        basicScenarioBuilder = new TestBasicScenarioNodeBuilder(scenarioName, depth + 1);
        return basicScenarioBuilder;
    }

    public TestBasicScenarioNodeBuilder setBasicScenario(TestBasicScenarioNodeBuilder scenarioBuilder) {

        basicScenarioBuilder = scenarioBuilder;
        return basicScenarioBuilder;
    }

    public OutlineScenarioRowNode build() {

        if (basicScenarioBuilder != null) {

            basicScenarioBuilder.addTags(tags);
        }

        BasicScenarioNode basicScenario = basicScenarioBuilder != null ? basicScenarioBuilder.build() : null;
        return built = new OutlineScenarioRowNode(rowIndex, basicScenario, tags, depth);
    }

    public OutlineScenarioRowNode getBuilt() {

        return built;
    }

    public void addTags(Set<String> tags) {

        this.tags.addAll(tags);
    }

}
