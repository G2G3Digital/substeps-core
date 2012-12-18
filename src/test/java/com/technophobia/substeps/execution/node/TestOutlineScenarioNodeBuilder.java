package com.technophobia.substeps.execution.node;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TestOutlineScenarioNodeBuilder implements TestScenarioNodeBuilder<OutlineScenarioNode> {

    private final String scenarioName;
    private final List<TestOutlineScenarioRowNodeBuilder> rowBuilders = Lists.newArrayList();
    private OutlineScenarioNode built;
    private final int depth;

    private final Set<String> tags = Sets.newHashSet();

    public TestOutlineScenarioNodeBuilder(String scenarioName, int depth) {

        this.scenarioName = scenarioName;
        this.depth = depth;
    }

    public TestOutlineScenarioRowNodeBuilder addRow(int index) {

        TestOutlineScenarioRowNodeBuilder builder = new TestOutlineScenarioRowNodeBuilder(index, depth + 1);
        rowBuilders.add(builder);
        return builder;
    }

    public OutlineScenarioNode build() {

        List<OutlineScenarioRowNode> outlineRows = Lists.newArrayListWithCapacity(rowBuilders.size());
        for (TestOutlineScenarioRowNodeBuilder builder : rowBuilders) {

            builder.addTags(tags);
            outlineRows.add(builder.build());
        }
        return built = new OutlineScenarioNode(scenarioName, outlineRows, depth);
    }

    public OutlineScenarioNode getBuilt() {

        return built;
    }

    public void addTags(Set<String> tags) {

        this.tags.addAll(tags);
    }

    public void addTag(String tag) {

        this.tags.add(tag);
    }

}
