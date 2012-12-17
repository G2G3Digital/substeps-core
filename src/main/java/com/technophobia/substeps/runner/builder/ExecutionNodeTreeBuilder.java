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
package com.technophobia.substeps.runner.builder;

import java.util.List;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.runner.TestParameters;

/**
 * @author ian
 * 
 */
public class ExecutionNodeTreeBuilder {

    private final TestParameters parameters;
    private final FeatureNodeBuilder featureNodeBuilder;

    public ExecutionNodeTreeBuilder(final TestParameters parameters) {
        this.parameters = parameters;
        this.featureNodeBuilder = new FeatureNodeBuilder(parameters);
    }

    public RootNode buildExecutionNodeTree() {

        List<FeatureNode> features = Lists.newArrayListWithExpectedSize(parameters.getFeatureFileList().size());

        for (final FeatureFile featureFile : parameters.getFeatureFileList()) {

            FeatureNode featureNode = featureNodeBuilder.build(featureFile);
            if (featureNode != null) {

                features.add(featureNode);
            }
        }

        return new RootNode(features);
    }

}
