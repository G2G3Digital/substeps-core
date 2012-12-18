/*
 *  Copyright Technophobia Ltd 2012
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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.runner.INotificationDistributor;
import com.technophobia.substeps.runner.SubstepExecutionFailure;
import com.technophobia.substeps.runner.TagManager;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;

public class NodeExecutionContext {

    private static final Logger log = LoggerFactory.getLogger(NodeExecutionContext.class);

    private boolean testsRun = false;
    private final INotificationDistributor notificationDistributor;
    private final List<SubstepExecutionFailure> failures;
    private final SetupAndTearDown setupAndTeardown;
    private final TagManager nonFatalTagmanager;
    private final MethodExecutor methodExecutor;

    public NodeExecutionContext(INotificationDistributor notificationDistributor,
            List<SubstepExecutionFailure> failures, SetupAndTearDown setupAndTeardown, TagManager nonFatalTagmanager,
            MethodExecutor methodExecutor) {

        this.notificationDistributor = notificationDistributor;
        this.failures = failures;
        this.setupAndTeardown = setupAndTeardown;
        this.nonFatalTagmanager = nonFatalTagmanager;
        this.methodExecutor = methodExecutor;
    }

    public INotificationDistributor getNotificationDistributor() {
        return notificationDistributor;
    }

    public List<SubstepExecutionFailure> getFailures() {
        return failures;
    }

    public SetupAndTearDown getSetupAndTeardown() {
        return setupAndTeardown;
    }

    public void addFailure(final SubstepExecutionFailure failure) {

        failures.add(failure);
        logFailure(failure);

        // set the criticality of this failure

        if (!failure.isSetupOrTearDown() && this.nonFatalTagmanager != null
                && nonFatalTagmanager.isApplicable(failure.getExeccutionNode())) {

            failure.setNonCritical(true);
        }

    }

    /**
     * @param failure
     */
    private void logFailure(final SubstepExecutionFailure failure) {

        final Throwable failureCause = failure.getCause();
        final Throwable here = new Throwable();

        final StackTraceElement[] failureTrace = failureCause.getStackTrace();
        final StackTraceElement[] hereTrace = here.getStackTrace();

        final int requiredTraceSize = failureTrace.length - hereTrace.length;

        if (requiredTraceSize > 0 && requiredTraceSize < failureTrace.length) {

            final StringBuilder stackTraceBuilder = new StringBuilder();

            stackTraceBuilder.append(failureCause.toString()).append("\n");

            for (int i = 0; i < requiredTraceSize; i++) {
                stackTraceBuilder.append("\tat ").append(failureTrace[i]).append("\n");
            }

            log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().toDebugString() + "\n"
                    + stackTraceBuilder.toString());

        } else {
            // fallback position - just normal logging
            log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().toDebugString(), failureCause);
        }

    }

    public MethodExecutor getMethodExecutor() {
        return methodExecutor;
    }

    public void setTestsHaveRun() {

        testsRun = true;
    }

    public boolean haveTestsBeenRun() {

        return testsRun;
    }

}
