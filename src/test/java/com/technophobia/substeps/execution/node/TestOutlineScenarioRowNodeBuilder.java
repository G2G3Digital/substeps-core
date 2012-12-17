package com.technophobia.substeps.execution.node;


public class TestOutlineScenarioRowNodeBuilder {

    private TestBasicScenarioNodeBuilder basicScenarioBuilder;
    
    
    private final int rowIndex;
    private final int depth;


    private OutlineScenarioRowNode built;

    public TestOutlineScenarioRowNodeBuilder(int rowIndex, int depth) {

        this.rowIndex = rowIndex;
        this.depth = depth;
    }
    
    public TestBasicScenarioNodeBuilder setBasicScenario(String scenarioName) {
        
        basicScenarioBuilder = new TestBasicScenarioNodeBuilder(scenarioName, depth + 1);
        return basicScenarioBuilder;
    }
    
    public OutlineScenarioRowNode build() {

        BasicScenarioNode basicScenario = basicScenarioBuilder != null ? basicScenarioBuilder.build() : null;
        return built = new OutlineScenarioRowNode(rowIndex, basicScenario, depth);
    }

    public OutlineScenarioRowNode getBuilt() {

        return built;
    }
    
}
