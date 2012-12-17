package com.technophobia.substeps.execution.node;


public class TestBasicScenarioNodeBuilder implements TestScenarioNodeBuilder<BasicScenarioNode> {

    private String scenarioName;
    private TestSubstepNodeBuilder backgroundBuilder;
    private TestSubstepNodeBuilder substepBuilder;

    private BasicScenarioNode built;
    private final int depth;

    public TestBasicScenarioNodeBuilder(String scenarioName, int depth) {

        this.scenarioName = scenarioName;
        this.depth = depth;
    }
    
    public TestSubstepNodeBuilder addBackground() {

        backgroundBuilder = new TestSubstepNodeBuilder(depth + 1);
        return backgroundBuilder;
    }
    
    public TestSubstepNodeBuilder addSubsteps() {
        
        substepBuilder = new TestSubstepNodeBuilder(depth + 1);
        return substepBuilder;
    }
    
    public BasicScenarioNode build() {
        
        SubstepNode backgroundNode = backgroundBuilder != null ? backgroundBuilder.build() : null;
        SubstepNode substepNode = substepBuilder != null ? substepBuilder.build() : null;
        
        return built = new BasicScenarioNode(scenarioName, backgroundNode, substepNode, depth);
    }

    public BasicScenarioNode getBuilt() {

        return built;
    }

}
