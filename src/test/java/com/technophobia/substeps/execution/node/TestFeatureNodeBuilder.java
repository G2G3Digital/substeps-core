package com.technophobia.substeps.execution.node;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.technophobia.substeps.execution.Feature;

public class TestFeatureNodeBuilder {

    private final Feature feature;
    
    private FeatureNode built;
    
    private Set<String> tags = Sets.newHashSet();

    List<TestScenarioNodeBuilder<?>> scenarioBuilders = Lists.newArrayList();

    public TestFeatureNodeBuilder(Feature feature) {
        this.feature = feature;
    }

    public TestBasicScenarioNodeBuilder addBasicScenario(String scenarioName) {

        TestBasicScenarioNodeBuilder testBasicScenarioNodeBuilder = new TestBasicScenarioNodeBuilder(scenarioName, 2);
        scenarioBuilders.add(testBasicScenarioNodeBuilder);
        return testBasicScenarioNodeBuilder;
    }
    
    public TestOutlineScenarioNodeBuilder addOutlineScenario(String scenarioName) {
        
        TestOutlineScenarioNodeBuilder outlineBuilder = new TestOutlineScenarioNodeBuilder(scenarioName, 2);
        scenarioBuilders.add(outlineBuilder);
        return outlineBuilder;
    }

    public FeatureNode build() {

        List<ScenarioNode<?>> scenarioNodes = Lists.newArrayListWithCapacity(scenarioBuilders.size());
        for (TestScenarioNodeBuilder<?> builder : scenarioBuilders) {
        
            scenarioNodes.add(builder.build());
        }
        built = new FeatureNode(feature, scenarioNodes);
        built.setTags(tags);
        return built;
    }

    public FeatureNode getBuilt() {
        
        return built;
    }

    public TestFeatureNodeBuilder addTags(String... tags) {

        this.tags.addAll(Arrays.asList(tags));
        return this;
    }
}
