package com.technophobia.substeps.runner;

import com.google.common.base.Strings;
import com.technophobia.substeps.execution.node.IExecutionNode;

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
public abstract class ExecutionLogger implements IExecutionListener {

    // TODO - buf the message, check the next one, see if its the same, don't
    // dupe

    private IExecutionNode theLastNode;

    public abstract void printFailed(String msg);

    public abstract void printPassed(String msg);

    public abstract void printSkipped(String msg);

    public abstract void printStarted(String msg);

    private final String indentString = "  ";

    private String format(final IExecutionNode node) {

        // TODO - no way of knowing of this is an impl or not..?
        return Strings.repeat(this.indentString, node.getDepth()) + node.getDescription() + " from "
                + node.getFilename();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeFailed(com.technophobia
     * .substeps.execution.node.IExecutionNode, java.lang.Throwable)
     */
    public void onNodeFailed(final IExecutionNode node, final Throwable t) {
        // TODO do something with the exception

        if (node.equals(this.theLastNode)) {
            printFailed("FAILED: " + format(this.theLastNode));
        } else {

            // print the last one first
            if (this.theLastNode != null) {
                printStarted("Starting: " + format(this.theLastNode));
            }

            printFailed("FAILED: " + format(node));
        }
        this.theLastNode = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.technophobia.substeps.runner.INotifier#notifyNodeFinished(com.
     * technophobia.substeps.execution.node.IExecutionNode)
     */
    public void onNodeFinished(final IExecutionNode node) {

        if (node.equals(this.theLastNode)) {
            printPassed("Passed: " + format(this.theLastNode));
        } else {

            // print the last one first
            if (this.theLastNode != null) {
                printStarted("Starting: " + format(this.theLastNode));
            }

            printPassed("Passed: " + format(node));
        }
        this.theLastNode = null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeIgnored(com.technophobia
     * .substeps.execution.node.IExecutionNode)
     */
    public void onNodeIgnored(final IExecutionNode node) {
        printSkipped(format(node));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeStarted(com.technophobia
     * .substeps.execution.node.IExecutionNode)
     */
    public void onNodeStarted(final IExecutionNode node) {

        if (!node.equals(this.theLastNode)) {

            if (this.theLastNode != null) {
                printStarted("Starting: " + format(this.theLastNode));
            }

            this.theLastNode = node;
        } else {
            // not sure how this would happen
            printStarted("Starting: " + format(node));
        }

    }

}
