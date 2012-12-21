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
