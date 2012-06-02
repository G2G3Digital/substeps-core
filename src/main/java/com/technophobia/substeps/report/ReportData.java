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

import com.technophobia.substeps.execution.ExecutionNode;


/**
 * @author ian
 * 
 */
public class ReportData {
    private List<ExecutionNode> rootNodes;

    public void addRootExecutionNode(final ExecutionNode node) {
        if (rootNodes == null) {
        	rootNodes = new ArrayList<ExecutionNode>();
        }
        rootNodes.add(node);
    }

    private void flattenTree(final List<ExecutionNode> nodeList, final ExecutionNode node) {
        
    	nodeList.add(node);

        if (node.hasChildren()) {
            for (final ExecutionNode child : node.getChildren()) {
                flattenTree(nodeList, child);
            }
        }
    }


    /**
     * @return the nodeList
     */
    public List<ExecutionNode> getNodeList() {
    	
    	final List<ExecutionNode> nodeList = new ArrayList<ExecutionNode>();
    	
    	for (final ExecutionNode rootNode: this.rootNodes){
    		flattenTree(nodeList, rootNode);
    	}
    			
        return nodeList;
    }

	/**
	 * @return the rootNodes
	 */
	public List<ExecutionNode> getRootNodes()
	{
		return rootNodes;
	}

}
