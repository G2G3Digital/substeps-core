package com.technophobia.substeps.execution.node;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ImplementationCache;
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
    private final MethodExecutor methodExecutor = new ImplementationCache();

    
    public NodeExecutionContext(INotificationDistributor notificationDistributor,
            List<SubstepExecutionFailure> failures, SetupAndTearDown setupAndTeardown, TagManager nonFatalTagmanager) {

        this.notificationDistributor = notificationDistributor;
        this.failures = failures;
        this.setupAndTeardown = setupAndTeardown;
        this.nonFatalTagmanager = nonFatalTagmanager;
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
                && nonFatalTagmanager.acceptTaggedScenario(failure.getExeccutionNode().getTags())) {

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

            //TODO RB 20121213 This should be using getDebugString
            log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().getDescription() + "\n"
                    + stackTraceBuilder.toString());

        } else {
            // fallback position - just normal logging
            //TODO RB 20121213 This should be using getDebugString
            log.info("SubstepExecutionFailure @ " + failure.getExeccutionNode().getDescription(),
                    failureCause);
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
