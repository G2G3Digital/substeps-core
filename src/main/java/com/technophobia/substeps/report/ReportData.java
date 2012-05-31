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
//    private DescriptorStatus status;
    private List<ExecutionNode> rootNodes;



    public void addRootExecutionNode(final ExecutionNode node) {
        if (rootNodes == null) {
        	rootNodes = new ArrayList<ExecutionNode>();
        }
        rootNodes.add(node);
    }

//    /**
//     * @deprecated
//     * @param rootNode
//     *            the rootNode to set
//     */ 
//    
//    @Deprecated
//	public void addDataFromRootNode(final ExecutionNode rootNode) {
////        flattenTree(rootNode);
//    	addRootExecutionNode(rootNode);
////        nodeList.remove(rootNode);
//
//    }

    /**
     * @param root
     * @param data
     */
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


//    public String getDetailsPageHref(final ExecutionNode node) {
//        return node.getId() + "-details.html";
//    }

    

//    public String getDescriptionForNode2(final ExecutionNode node) {
//        final StringBuilder buf = new StringBuilder();
//        
//        if (node.getParent() == null){
//        	buf.append(0).append(", \"");
//        	
//        	if (node.getLine() != null){
//        		buf.append(node.getLine());
//        	}
//        	else {
//        		buf.append("executionNodeRoot\"");
//        	}
//        }
//        else
//        {
//        
//        buf.append(status.getIndexStringForNode(node)).append(": ");
//
//        if (node.getFeature() != null) {
//
//            // buf.append("F: ").append(status.featureCount).append(": ")
//            buf.append(node.getFeature().getName());
//
//        } else if (node.getScenarioName() != null) {
//
//            if (node.isOutlineScenario()) {
//                buf.append("ScnO: ");
//            } else {
//                buf.append("Scn: ");
//            }
//            // buf.append(status.featureCount).append("-").append(status.scenarioCount).append(": ")
//            buf.append(node.getScenarioName());
//        }
//
//        if (node.getParent() != null && node.getParent().isOutlineScenario()) {
//
//            // buf.append("ScnO:").append(status.featureCount).append("-")
//            // .append(status.scenarioCount).append("-")
//            buf.append(node.getRowNumber()).append(" ").append(node.getParent().getScenarioName())
//                    .append(":");
//        }
//
//        if (node.getLine() != null) {
//            // buf.append("ScnO:").append(status.featureCount).append("-")
//            // .append(status.scenarioCount).append("-").append(status.stepCount).append(": ")
//            buf.append(node.getLine());
//        }
//
//        String rtn = buf.toString();
//        if (rtn.contains("\"")) {
//
//            rtn = "'" + rtn + "'";
//        } else {
//            rtn = "\"" + rtn + "\"";
//        }
//        }
//        return StringEscapeUtils.escapeHtml(buf.toString());
//    }
    
//    public String getDescriptionForNode(final ExecutionNode node) {
//        final StringBuilder buf = new StringBuilder();
//
//        // TODO - think on Jenkins the report looks like the dot is being
//        // interpreted as package delimiter
//
//        buf.append(status.getIndexStringForNode(node)).append(": ");
//
//        if (node.getFeature() != null) {
//
//            // buf.append("F: ").append(status.featureCount).append(": ")
//            buf.append(node.getFeature().getName());
//
//        } else if (node.getScenarioName() != null) {
//
//            if (node.isOutlineScenario()) {
//                buf.append("ScnO: ");
//            } else {
//                buf.append("Scn: ");
//            }
//            // buf.append(status.featureCount).append("-").append(status.scenarioCount).append(": ")
//            buf.append(node.getScenarioName());
//        }
//
//        if (node.getParent() != null && node.getParent().isOutlineScenario()) {
//
//            // buf.append("ScnO:").append(status.featureCount).append("-")
//            // .append(status.scenarioCount).append("-")
//            buf.append(node.getRowNumber()).append(" ").append(node.getParent().getScenarioName())
//                    .append(":");
//        }
//
//        if (node.getLine() != null) {
//            // buf.append("ScnO:").append(status.featureCount).append("-")
//            // .append(status.scenarioCount).append("-").append(status.stepCount).append(": ")
//            buf.append(node.getLine());
//        }
//
//        String rtn = buf.toString();
//        if (rtn.contains("\"")) {
//
//            rtn = "'" + rtn + "'";
//        } else {
//            rtn = "\"" + rtn + "\"";
//        }
//        
//        // escape characters
//        
////        return rtn;
//        return buf.toString();
//    }

    
//    public String getAddTreeNodeStr(final ExecutionNode node) {
//        final StringBuilder buf = new StringBuilder();
//
//        // need to render root nodes differently
//        
//        buf.append("d.add(")
//        .append(node.getId())
//        .append(",");
//        
//        if (node.getParent() == null){
//        	buf.append(0).append(", \"");
//        	
//        	if (node.getLine() != null){
//        		buf.append(node.getLine());
//        	}
//        	else {
//        		buf.append("executionNodeRoot\"");
//        	}
//        }
//        else{
//        	buf.append(node.getParent().getId()).append(", \"")
//            .append(StringEscapeUtils.escapeHtml(getDescriptionForNode(node)));	
//        }
//        
//        
//        buf
//        .append("\",")
//        .append("\"")
//        .append(getDetailsPageHref(node))
//        .append("\", '', 'detailsFrame',\"")
//        .append(getNodeImage(node))
//        .append("\",\"")
//        .append(getNodeImage(node))
//        .append("\");");	
//        
//        return buf.toString();
//        
//    }
    /**
     * @return the status
     */
//    public DescriptorStatus getStatus() {
//        return status;
//    }


    /**
     * @param status
     *            the status to set
     */
//    public void setStatus(final DescriptorStatus status) {
//        this.status = status;
//    }

	/**
	 * @return the rootNodes
	 */
	public List<ExecutionNode> getRootNodes()
	{
		return rootNodes;
	}

}
