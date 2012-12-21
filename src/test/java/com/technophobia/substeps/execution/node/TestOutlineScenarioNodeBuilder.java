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
        return built = new OutlineScenarioNode(scenarioName, outlineRows, tags, depth);
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
