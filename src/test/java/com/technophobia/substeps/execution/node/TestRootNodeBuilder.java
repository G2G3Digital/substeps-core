package com.technophobia.substeps.execution.node;

import java.util.List;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.Feature;

public class TestRootNodeBuilder {

    private final List<TestFeatureNodeBuilder> featureBuilders = Lists.newArrayList();

    private final String description;

    public TestRootNodeBuilder(String description) {

        this.description = description;
    }

    public TestRootNodeBuilder() {
        this("Root node description");
    }

    public TestFeatureNodeBuilder addFeature(Feature feature) {

        TestFeatureNodeBuilder featureNodeBuilder = new TestFeatureNodeBuilder(feature);
        featureBuilders.add(featureNodeBuilder);
        return featureNodeBuilder;
    }

    public RootNode build() {

        List<FeatureNode> featureNodes = Lists.newArrayListWithExpectedSize(featureBuilders.size());
        for (TestFeatureNodeBuilder builder : featureBuilders) {
            featureNodes.add(builder.build());
        }

        return new RootNode(description, featureNodes);
    }

}
