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
package com.technophobia.substeps.execution;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * represents a node on the tree of features, scenarios, substeps etc including
 * outlines and backgrounds
 * 
 * @author ian
 * 
 */
public class ExecutionNode {

    private static AtomicLong counter = new AtomicLong(1);

    private final long id; // for uniqueness

    private Feature feature = null;

    // could be null or the name of feature or the substeps file
    private String filename;

    private String scenarioName = null;

    private int depth = 0;
    private int rowNumber = -1;

    private List<ExecutionNode> children = null;
    private List<ExecutionNode> backgrounds = null;

    private ExecutionNode parent = null;

    private Class<?> targetClass = null;
    private Method targetMethod = null;
    private Object[] methodArgs = null;

    private String line;
    private boolean background = false;

    private boolean outline = false;

    private final ExecutionNodeResult result = new ExecutionNodeResult();

    private Set<String> tags; // used for analysis


    public ExecutionNode() {
        id = counter.getAndIncrement();
    }


    public void addChild(final ExecutionNode child) {
        child.setParent(this);
        child.setDepth(depth + 1);

        if (children == null) {
            children = new ArrayList<ExecutionNode>();
        }
        children.add(child);
    }


    public void addBackground(final ExecutionNode backgroundNode) {
        if (backgrounds == null) {
            backgrounds = new ArrayList<ExecutionNode>();
        }
        backgroundNode.background = true;
        backgrounds.add(backgroundNode);

        backgroundNode.setDepth(depth + 1);

    }


    /**
     * @return the feature
     */
    public Feature getFeature() {
        return feature;
    }


    /**
     * @param feature
     *            the feature to set
     */
    public void setFeature(final Feature feature) {
        this.feature = feature;
    }


    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }


    /**
     * @param depth
     *            the depth to set
     */
    public void setDepth(final int depth) {
        this.depth = depth;
    }


    /**
     * @return the rowNumber
     */
    public int getRowNumber() {
        return rowNumber;
    }


    /**
     * @param rowNumber
     *            the rowNumber to set
     */
    public void setRowNumber(final int rowNumber) {
        this.rowNumber = rowNumber;
    }


    /**
     * @return the parent
     */
    public ExecutionNode getParent() {
        return parent;
    }


    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(final ExecutionNode parent) {

        if (parent.getId() == getId()) {
            throw new IllegalStateException("don't think so");
        }

        this.parent = parent;
    }


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }


    /**
     * @return the targetClass
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }


    /**
     * @param targetClass
     *            the targetClass to set
     */
    public void setTargetClass(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }


    /**
     * @return the methodArgs
     */
    public Object[] getMethodArgs() {
        return methodArgs;
    }


    /**
     * @param methodArgs
     *            the methodArgs to set
     */
    public void setMethodArgs(final Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }


    /**
     * @return the scenarioName
     */
    public String getScenarioName() {
        return scenarioName;
    }


    /**
     * @param scenarioName
     *            the scenarioName to set
     */
    public void setScenarioName(final String scenarioName) {
        this.scenarioName = scenarioName;
    }


    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }


    /**
     * @param line
     *            the line to set
     */
    public void setLine(final String line) {
        this.line = line;
    }


    /**
     * @return the targetMethod
     */
    public Method getTargetMethod() {
        return targetMethod;
    }


    /**
     * @param targetMethod
     *            the targetMethod to set
     */
    public void setTargetMethod(final Method targetMethod) {
        this.targetMethod = targetMethod;
    }


    /**
     * @return the children
     */
    public List<ExecutionNode> getChildren() {
        return children;
    }


    /**
     * @return the backgrounds
     */
    public List<ExecutionNode> getBackgrounds() {
        return backgrounds;
    }


    public String printTree() {
        // traverse the tree
        final StringBuilder buf = new StringBuilder();

        buf.append("Execution tree:\n");

        buf.append(toDebugString()).append("\n");

        if (children != null) {
            for (final ExecutionNode child : children) {
                buf.append(child.toDebugString());
            }
        }

        return buf.toString();
    }


    @Override
    public String toString() {
        return id + ":" + getDescription() + " children size: "
                + getChildrenSize();
    }


    public String treeToString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(toString());
        buf.append("\n");
        if (hasChildren()) {

            for (final ExecutionNode child : getChildren()) {

                buf.append(Strings.repeat("\t", depth));
                buf.append(child.treeToString());
                buf.append("\n");
            }
        }

        return buf.toString();
    }


    public String getDebugStringForThisNode() {
        final StringBuilder buf = new StringBuilder();

        buf.append(id);

        if (parent != null) {
            buf.append(Strings.repeat("\t", depth));

            if (feature != null) {
                buf.append(feature.getName()).append(" in ")
                        .append(feature.getFilename()).append("\n");
            } else if (scenarioName != null) {
                buf.append(scenarioName).append("\n");
            }

            if (rowNumber > -1) {
                buf.append("outline #: ").append(rowNumber).append("\n");
            }

            if (background) {
                buf.append("BACKGROUND\n");
            }

            if (line != null) {
                buf.append(line);
            }

            appendMethodInfo("  -  ", buf);

        } else {
            buf.append(": Root");
        }
        return buf.toString();
    }


    /**
     * @return
     */
    public String toDebugString() {
        final StringBuilder buf = new StringBuilder();

        if (parent != null) {

            buf.append(id).append(Strings.repeat("\t", depth)).append("file: ")
                    .append(getFilename()).append(" ").append(parent.getId())
                    .append(" ");

            if (feature != null) {
                buf.append(feature.getName()).append("\n");
            } else if (scenarioName != null) {
                buf.append(scenarioName).append("\n");
            }

            if (rowNumber > -1) {
                buf.append(" outline #: ").append(rowNumber).append("\n");
            }

            if (background) {
                buf.append("BACKGROUND\n");
            }

            if (backgrounds != null) {
                for (final ExecutionNode backgroundNode : backgrounds) {
                    buf.append(backgroundNode.toDebugString());
                }
            }

            boolean printedLine = false;
            if (line != null) {
                buf.append(line);
                printedLine = true;
            }

            appendMethodInfo("  -  ", buf);

            if (printedLine) {
                buf.append("\n");
            }
        }

        if (children != null) {
            for (final ExecutionNode child : children) {
                buf.append(child.toDebugString());
            }
        }

        // else we're root
        return buf.toString();
    }


    public void appendMethodInfo(final StringBuilder buf) {
        appendMethodInfo(null, buf);
    }


    /**
     * @param buf
     */
    public void appendMethodInfo(final String prefix, final StringBuilder buf) {
        if (targetClass != null && targetMethod != null) {

            if (prefix != null) {
                buf.append(prefix);
            }

            buf.append(targetClass.getSimpleName()).append(".")
                    .append(targetMethod.getName()).append("(");

            if (methodArgs != null) {
                boolean commaRequired = false;
                for (final Object arg : methodArgs) {
                    if (commaRequired) {
                        buf.append(", ");
                    }

                    boolean quotes = false;
                    if (arg instanceof String) {
                        quotes = true;
                        buf.append("\"");
                    }
                    buf.append(arg.toString());
                    if (quotes) {
                        buf.append("\"");
                    }
                    commaRequired = true;
                }
            }

            buf.append(")");
        }
    }


    /**
     * @return
     */
    public boolean isOutlineScenario() {
        return outline;
    }


    /**
     * @param b
     */
    public void setOutline(final boolean isOutline) {
        outline = isOutline;

    }


    /**
     * @return
     */
    public boolean hasBackground() {
        return backgrounds != null && !backgrounds.isEmpty();
    }


    /**
     * @return
     */
    public boolean isExecutable() {
        return getTargetClass() != null && getTargetMethod() != null;
    }


    /**
     * @return
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }


    /**
     * @return
     */
    public Long getLongId() {
        return Long.valueOf(id);
    }


    /**
     * @return
     */
    public boolean shouldHaveChildren() {
        return isFeature() || isScenario() || isOutlineScenario();
    }


    /**
     * @return
     */
    public boolean isScenario() {
        return scenarioName != null;
    }


    /**
     * @return
     */
    public boolean isFeature() {
        return feature != null;
    }


    /**
     * @return the result
     */
    public ExecutionNodeResult getResult() {
        return result;
    }


    /**
     * @return the tags
     */
    public Set<String> getTags() {
        return tags;
    }


    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }


    /**
     * 
     */
    public boolean isStep() {

        return depth == 3 && !isOutlineScenario() || depth == 4
                && parent.isOutlineScenario();
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int res = 1;
        res = prime * res + (int) (id ^ (id >>> 32));
        return res;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionNode other = (ExecutionNode) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }


    /**
     * @return the filename
     */
    public String getFilename() {
        // use this filename if specified, or go up through the tree till we get
        // an answer

        if (filename != null) {
            return filename;
        } else if (parent != null) {
            filename = parent.getFilename();
            return filename;
        } else {
            filename = "";
        }

        return filename;
    }


    /**
     * @param filename
     *            the filename to set
     */
    public void setFilename(final String filename) {
        this.filename = filename;
    }


    public String getType() {

        String rtn = null;
        if (parent == null) {
            rtn = "Root node";
        } else if (isFeature()) {
            rtn = "Feature";
        } else if (isScenario()) {
            rtn = "Scenario";
        } else if (isOutlineScenario()) {
            rtn = "Scenario Outline";
        } else if (isStep()) {
            rtn = "Step";
        } else if (targetMethod != null) {
            rtn = "Step Implementation";
        }
        return rtn;

    }


    public String getDescription() {

        // return a string that represents what this is
        String rtn = null;

        if (line != null) {
            rtn = line;
        } else {
            if (isFeature()) {

                rtn = feature.getName();
            } else if (isScenario()) {

                rtn = scenarioName;
            } else if (parent != null && parent.isOutlineScenario()) {
                rtn = parent.scenarioName + " [" + rowNumber + "]";
            }
        }
        return rtn;
    }


    public boolean hasError() {
        return result.getResult() == ExecutionResult.FAILED
                || result.getResult() == ExecutionResult.PARSE_FAILURE;
    }


    public boolean hasPassed() {
        return result.getResult() == ExecutionResult.PASSED;
    }


    public Set<String> getTagsFromHierarchy() {
        Set<String> allTags = null;

        ExecutionNode node = this;

        while (node != null) {

            if (node.tags != null) {

                if (allTags == null) {
                    allTags = new HashSet<String>();
                }
                allTags.addAll(node.tags);
            }

            node = node.parent;
        }

        return allTags;
    }


    public List<ExecutionNode> getFailedChildNodes() {

        // TODO - how should we handle background or setup and tear down
        // failures ?

        final List<ExecutionNode> failed = filterNodes(children,
                new Predicate<ExecutionNode>() {

                    public boolean apply(final ExecutionNode input) {

                        return input.hasFailed();
                    }
                });

        return failed;
    }


    private List<ExecutionNode> filterNodes(
            final List<ExecutionNode> sourceList,
            final Predicate<ExecutionNode> predicate) {

        List<ExecutionNode> filtered = null;
        if (sourceList != null) {

            for (final ExecutionNode node : sourceList) {

                if (predicate.apply(node)) {
                    if (filtered == null) {
                        filtered = new ArrayList<ExecutionNode>();
                    }
                    filtered.add(node);
                }
            }
        }
        return filtered;
    }


    /**
     * @return
     */
    private boolean hasFailed() {

        // this node has failed if any of this node's backgrounds have failed,
        // this node's state is failed, or any of this node's children's state
        // has failed
        // TODO include backgrounds
        return result.getResult() == ExecutionResult.FAILED
                || getFailedChildNodes() != null;
    }


    /**
     * @param i
     */
    public ExecutionNode getChild(final int i) {
        ExecutionNode rtn = null;
        if (children != null && children.size() > i) {
            rtn = children.get(i);
        }
        return rtn;
    }


    /**
     * @return
     */
    public int getChildrenSize() {
        int rtn = 0;

        if (children != null) {
            rtn = children.size();
        }

        return rtn;
    }

}
