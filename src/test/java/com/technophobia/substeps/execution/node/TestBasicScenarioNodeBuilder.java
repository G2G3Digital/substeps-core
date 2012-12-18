package com.technophobia.substeps.execution.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class TestBasicScenarioNodeBuilder implements TestScenarioNodeBuilder<BasicScenarioNode> {

    private final String scenarioName;
    private TestSubstepNodeBuilder backgroundBuilder;
    private TestSubstepNodeBuilder substepBuilder;
    private final Set<String> tags = Sets.newHashSet();

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

    public void addTag(String tag) {

        this.tags.add(tag);
    }

    public void addTags(Set<String> tags) {

        this.tags.addAll(tags);
    }

    public BasicScenarioNode build() {

        SubstepNode backgroundNode = backgroundBuilder != null ? backgroundBuilder.build() : null;
        SubstepNode substepNode = substepBuilder != null ? substepBuilder.build() : null;

        return built = new BasicScenarioNode(scenarioName, backgroundNode, substepNode, tags, depth);
    }

    public BasicScenarioNode getBuilt() {

        return built;
    }

}
