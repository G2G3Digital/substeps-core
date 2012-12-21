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
package com.technophobia.substeps.report;

import java.util.ArrayList;
import java.util.List;

import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.RootNode;

/**
 * @author ian
 * 
 */
class ReportData {

    private List<RootNode> rootNodes;

    public void addRootExecutionNode(final RootNode node) {
        if (rootNodes == null) {
            rootNodes = new ArrayList<RootNode>();
        }
        rootNodes.add(node);
    }

    private List<IExecutionNode> flattenTree(final IExecutionNode node) {

        return node.accept(new AbstractExecutionNodeVisitor<IExecutionNode>() {
            
            @Override
            public IExecutionNode visit(IExecutionNode node) {
                return node;
            }
        });
    }

    /**
     * @return the nodeList
     */
    public List<IExecutionNode> getNodeList() {

        final List<IExecutionNode> nodeList = new ArrayList<IExecutionNode>();

        for (final ExecutionNode rootNode : this.rootNodes) {
            nodeList.addAll(flattenTree(rootNode));
        }

        return nodeList;
    }

    /**
     * @return the rootNodes
     */
    public List<RootNode> getRootNodes() {
        return rootNodes;
    }

}
