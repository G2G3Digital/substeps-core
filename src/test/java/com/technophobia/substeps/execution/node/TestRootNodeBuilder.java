/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
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
