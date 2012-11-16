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

import java.io.File;
import java.io.Serializable;
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
public class ExecutionNode implements Serializable {

    private static final long serialVersionUID = 4981517213059529046L;

    private static transient AtomicLong counter = new AtomicLong(1);

    private final long id; // for uniqueness

    private Feature feature = null;

    /**
     * An {@link ExecutionNode} can be seen as compiled substeps code - ready to
     * run. We include the fileUri and line number to tie this back to the
     * substeps source - the files from which the compiled substeps were
     * generated. This information could be seen as debug information - however
     * it is useful in other places - for example in editor plugins where we
     * have the compiled code but need to show where it came from to the user.
     * Note that these values won't always exist, for example the root node.
     * 
     */
    private String fileUri;
    private int lineNumber;

    private String scenarioName = null;

    private int depth = 0;
    private int rowNumber = -1;

    private List<ExecutionNode> children = null;
    private List<ExecutionNode> backgrounds = null;

    private ExecutionNode parent = null;

    // TODO - do we need to serialize this info - because we can't serialize the
    // method or potentially the methodargs!

    private transient Class<?> targetClass = null;
    private transient Method targetMethod = null;
    private transient Object[] methodArgs = null;

    private String line;
    private boolean background = false;

    private boolean outline = false;

    private final ExecutionNodeResult result;

    private Set<String> tags; // used for analysis


    public ExecutionNode() {
        this.id = counter.getAndIncrement();
        this.result = new ExecutionNodeResult(this.id);
    }


    public void addChild(final ExecutionNode child) {
        child.setParent(this);
        child.setDepth(this.depth + 1);

        if (this.children == null) {
            this.children = new ArrayList<ExecutionNode>();
        }
        this.children.add(child);
    }


    public void addBackground(final ExecutionNode backgroundNode) {
        if (this.backgrounds == null) {
            this.backgrounds = new ArrayList<ExecutionNode>();
        }
        backgroundNode.background = true;
        this.backgrounds.add(backgroundNode);

        backgroundNode.setDepth(this.depth + 1);

    }


    /**
     * @return the feature
     */
    public Feature getFeature() {
        return this.feature;
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
        return this.depth;
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
        return this.rowNumber;
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
        return this.parent;
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
        return this.id;
    }


    /**
     * @return the targetClass
     */
    public Class<?> getTargetClass() {
        return this.targetClass;
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
        return this.methodArgs;
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
        return this.scenarioName;
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
        return this.line;
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
        return this.targetMethod;
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
        return this.children;
    }


    /**
     * @return the backgrounds
     */
    public List<ExecutionNode> getBackgrounds() {
        return this.backgrounds;
    }


    public String printTree() {
        // traverse the tree
        final StringBuilder buf = new StringBuilder();

        buf.append("Execution tree:\n");

        buf.append(toDebugString()).append("\n");

        if (this.children != null) {
            for (final ExecutionNode child : this.children) {
                buf.append(child.toDebugString());
            }
        }

        return buf.toString();
    }


    @Override
    public String toString() {
        return this.id + ":" + getDescription() + " children size: "
                + getChildrenSize();
    }


    public String treeToString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(toString());
        buf.append("\n");
        if (hasChildren()) {

            for (final ExecutionNode child : getChildren()) {

                buf.append(Strings.repeat("\t", this.depth));
                buf.append(child.treeToString());
                buf.append("\n");
            }
        }

        return buf.toString();
    }


    public String getDebugStringForThisNode() {
        final StringBuilder buf = new StringBuilder();

        buf.append(this.id);

        if (this.parent != null) {
            buf.append(Strings.repeat("\t", this.depth));

            if (this.feature != null) {
                buf.append(this.feature.getName()).append(" in ")
                        .append(this.feature.getFilename()).append("\n");
            } else if (this.scenarioName != null) {
                buf.append(this.scenarioName).append("\n");
            }

            if (this.rowNumber > -1) {
                buf.append("outline #: ").append(this.rowNumber).append("\n");
            }

            if (this.background) {
                buf.append("BACKGROUND\n");
            }

            if (this.line != null) {
                buf.append(this.line);
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

        if (this.parent != null) {

            buf.append(this.id).append(Strings.repeat("\t", this.depth))
                    .append("file: ").append(getFilename()).append(" ")
                    .append(this.parent.getId()).append(" ");

            if (this.feature != null) {
                buf.append(this.feature.getName()).append("\n");
            } else if (this.scenarioName != null) {
                buf.append(this.scenarioName).append("\n");
            }

            if (this.rowNumber > -1) {
                buf.append(" outline #: ").append(this.rowNumber).append("\n");
            }

            if (this.background) {
                buf.append("BACKGROUND\n");
            }

            if (this.backgrounds != null) {
                for (final ExecutionNode backgroundNode : this.backgrounds) {
                    buf.append(backgroundNode.toDebugString());
                }
            }

            boolean printedLine = false;
            if (this.line != null) {
                buf.append(this.line);
                printedLine = true;
            }

            appendMethodInfo("  -  ", buf);

            if (printedLine) {
                buf.append("\n");
            }
        }

        if (this.children != null) {
            for (final ExecutionNode child : this.children) {
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
        if (this.targetClass != null && this.targetMethod != null) {

            if (prefix != null) {
                buf.append(prefix);
            }

            buf.append(this.targetClass.getSimpleName()).append(".")
                    .append(this.targetMethod.getName()).append("(");

            if (this.methodArgs != null) {
                boolean commaRequired = false;
                for (final Object arg : this.methodArgs) {
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
        return this.outline;
    }


    /**
     * @param b
     */
    public void setOutline(final boolean isOutline) {
        this.outline = isOutline;

    }


    /**
     * @return
     */
    public boolean hasBackground() {
        return this.backgrounds != null && !this.backgrounds.isEmpty();
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
        return this.children != null && !this.children.isEmpty();
    }


    /**
     * @return
     */
    public Long getLongId() {
        return Long.valueOf(this.id);
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
        return this.scenarioName != null;
    }


    /**
     * @return
     */
    public boolean isFeature() {
        return this.feature != null;
    }


    /**
     * @return the result
     */
    public ExecutionNodeResult getResult() {
        return this.result;
    }


    /**
     * @return the tags
     */
    public Set<String> getTags() {
        return this.tags;
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

        return this.depth == 3 && !isOutlineScenario() || this.depth == 4
                && this.parent.isOutlineScenario();
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
        res = prime * res + (int) (this.id ^ (this.id >>> 32));
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }


    /**
     * @return the filename
     */
    public String getFilename() {
        return new File(getFileUri()).getName();
    }


    public String getFileUri() {
        // Use this filename if specified, or go up through the tree till we get
        // an answer
        if (this.fileUri != null) {
            return this.fileUri;
        } else if (this.parent != null) {
            this.fileUri = this.parent.getFileUri();
            return this.fileUri;
        } else {
            this.fileUri = "";
        }

        return this.fileUri;
    }


    public void setFileUri(final String fileUri) {
        this.fileUri = fileUri;
    }


    public int getLineNumber() {
        return this.lineNumber;
    }


    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public String getType() {

        String rtn = null;
        if (this.parent == null) {
            rtn = "Root node";
        } else if (isFeature()) {
            rtn = "Feature";
        } else if (isScenario()) {
            rtn = "Scenario";
        } else if (isOutlineScenario()) {
            rtn = "Scenario Outline";
        } else if (isStep()) {
            rtn = "Step";
        } else if (this.targetMethod != null) {
            rtn = "Step Implementation";
        }
        return rtn;

    }


    public String getDescription() {

        // return a string that represents what this is
        String rtn = null;

        if (this.line != null) {
            rtn = this.line;
        } else {
            if (isFeature()) {

                rtn = this.feature.getName();
            } else if (isScenario()) {

                rtn = this.scenarioName;
            } else if (this.parent != null && this.parent.isOutlineScenario()) {
                rtn = this.parent.scenarioName + " [" + this.rowNumber + "]";
            }
        }
        return rtn;
    }


    public boolean hasError() {
        return this.result.getResult() == ExecutionResult.FAILED
                || this.result.getResult() == ExecutionResult.PARSE_FAILURE;
    }


    public boolean hasPassed() {
        return this.result.getResult() == ExecutionResult.PASSED;
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

        final List<ExecutionNode> failed = filterNodes(this.children,
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
        return this.result.getResult() == ExecutionResult.FAILED
                || getFailedChildNodes() != null;
    }


    /**
     * @param i
     */
    public ExecutionNode getChild(final int i) {
        ExecutionNode rtn = null;
        if (this.children != null && this.children.size() > i) {
            rtn = this.children.get(i);
        }
        return rtn;
    }


    /**
     * @return
     */
    public int getChildrenSize() {
        int rtn = 0;

        if (this.children != null) {
            rtn = this.children.size();
        }

        return rtn;
    }

}
