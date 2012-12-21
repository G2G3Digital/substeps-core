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

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.collect.Sets;

public class TestStepImplementationNodeBuilder implements TestStepNodeBuilder<StepImplementationNode> {

    private final StepImplementationNode stepImplementationNode;

    private final Set<String> tags = Sets.newHashSet();

    public TestStepImplementationNodeBuilder(Class<?> targetClass, Method targetMethod, int depth, Object... methodArgs) {

        this.stepImplementationNode = new StepImplementationNode(targetClass, targetMethod, tags, depth);
        stepImplementationNode.setMethodArgs(methodArgs);
    }

    public TestStepImplementationNodeBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public StepImplementationNode build() {

        return stepImplementationNode;
    }
}
