package com.technophobia.substeps.execution.node;

import java.util.List;

import com.google.common.collect.Lists;


public class TestOutlineScenarioNodeBuilder implements TestScenarioNodeBuilder<OutlineScenarioNode> {

    
    private final String scenarioName;
    private List<TestOutlineScenarioRowNodeBuilder> rowBuilders = Lists.newArrayList();
    private OutlineScenarioNode built;
    private final int depth;

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
        for(TestOutlineScenarioRowNodeBuilder builder : rowBuilders) {
            outlineRows.add(builder.build());
        }
        return built = new OutlineScenarioNode(scenarioName, outlineRows, depth);
    }

    public OutlineScenarioNode getBuilt() {

        return built;
    }

}
