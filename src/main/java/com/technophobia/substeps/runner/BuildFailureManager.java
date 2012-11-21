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

package com.technophobia.substeps.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.technophobia.substeps.execution.ExecutionNode;

/**
 * @author ian
 * 
 */
public class BuildFailureManager {

    private List<SubstepExecutionFailure> criticalFailures = null;
    private List<SubstepExecutionFailure> nonCriticalFailures = null;

    public String getBuildFailureInfo() {
        return getBuildInfoString("NON CRITICAL FAILURES:\n\n", this.nonCriticalFailures)
                + getBuildInfoString("\n\nCRITICAL FAILURES:\n\n", this.criticalFailures);
    }

    private String getBuildInfoString(final String msg, final List<SubstepExecutionFailure> failures) {
        final StringBuilder buf = new StringBuilder();

        final Set<ExecutionNode> dealtWith = new HashSet<ExecutionNode>();

        if (failures != null && !failures.isEmpty()) {

            buf.append(msg);

            for (final SubstepExecutionFailure fail : failures) {
                final ExecutionNode node = fail.getExeccutionNode();
                if (!dealtWith.contains(node)) {
                    final List<ExecutionNode> hierarchy = new ArrayList<ExecutionNode>();

                    hierarchy.add(node);

                    // go up the tree as far as we can go
                    ExecutionNode parent = node.getParent();
                    while (parent != null) {
                        hierarchy.add(parent);
                        parent = parent.getParent();
                    }

                    Collections.reverse(hierarchy);

                    for (final ExecutionNode node2 : hierarchy) {
                        buf.append(node2.getDebugStringForThisNode());
                        dealtWith.add(node2);
                    }
                }
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    /**
     * @param failures
     */
    public void sortFailures(final List<SubstepExecutionFailure> failures) {

        for (final SubstepExecutionFailure fail : failures) {

            if (fail.isNonCritical()) {

                if (this.nonCriticalFailures == null) {
                    this.nonCriticalFailures = new ArrayList<SubstepExecutionFailure>();
                }

                this.nonCriticalFailures.add(fail);
            } else {

                if (this.criticalFailures == null) {
                    this.criticalFailures = new ArrayList<SubstepExecutionFailure>();
                }

                criticalFailures.add(fail);
            }
        }
    }

    public boolean testSuiteCompletelyPassed() {
        return (this.criticalFailures == null && this.nonCriticalFailures == null)
                ||

                (this.criticalFailures != null && this.criticalFailures.isEmpty() && this.nonCriticalFailures != null && this.nonCriticalFailures
                        .isEmpty());
    }

    public boolean testSuiteSomeFailures() {
        return (testSuiteFailed()) || (this.nonCriticalFailures != null && !this.nonCriticalFailures.isEmpty());
    }

    public boolean testSuiteFailed() {
        return (this.criticalFailures != null && !this.criticalFailures.isEmpty());
    }
}
