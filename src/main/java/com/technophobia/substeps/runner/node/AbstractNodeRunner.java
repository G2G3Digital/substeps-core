package com.technophobia.substeps.runner.node;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.runner.SubstepExecutionFailure;

public abstract class AbstractNodeRunner<NODE_TYPE extends IExecutionNode, VISITOR_RETURN_TYPE> extends
        AbstractExecutionNodeVisitor<VISITOR_RETURN_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(AbstractNodeRunner.class);

    public final boolean run(NODE_TYPE node, RootNodeExecutionContext context) {

        boolean success = false;

        if (beforeExecute(node, context)) {
        	try {
        		
        		success = execute(node, context);
        	
        	} finally {

        		afterExecute(node, success, context);
        	}
        }

        return success;
    }

    private boolean beforeExecute(NODE_TYPE node, RootNodeExecutionContext context) {

        boolean shouldContinue = true;

        context.getNotificationDistributor().notifyNodeStarted(node);

        if (node.hasError()) {

            context.getNotificationDistributor().notifyNodeFailed(node, node.getResult().getThrown());
            context.addFailure(new SubstepExecutionFailure(node.getResult().getThrown(), node));
            shouldContinue = false;

        } else {
            node.getResult().setStarted();
            shouldContinue = runSetup(node, context);
        }

        return shouldContinue;
    }

    private void afterExecute(NODE_TYPE node, boolean success, RootNodeExecutionContext context) {

        recordResult(node, success, context);
        runTearDown(node, context);
    }

    protected abstract boolean execute(NODE_TYPE node, RootNodeExecutionContext context);

    protected abstract Scope getScope();

    private boolean runSetup(NODE_TYPE node, RootNodeExecutionContext context) {

        try {
            context.getSetupAndTeardown().runSetup(getScope());
            return true;
        } catch (final Throwable t) {

            log.debug("setup failed", t);
            context.addFailure(new SubstepExecutionFailure(t, node, true));
            return false;
        }
    }

    private void recordResult(NODE_TYPE node, boolean success, RootNodeExecutionContext context) {

        if (success) {
            if (log.isTraceEnabled()) {

                log.trace("node success");
            }

            context.getNotificationDistributor().notifyNodeFinished(node);

            node.getResult().setFinished();

        } else {

            List<SubstepExecutionFailure> failures = context.getFailures();

            if (log.isDebugEnabled()) {

                log.debug("node failures");
            }

            SubstepExecutionFailure lastFailure = failures.get(failures.size() - 1);
            // just notify on the last one in..?
            final Throwable lastException = lastFailure.getCause();
            context.getNotificationDistributor().notifyNodeFailed(node, lastException);

            // TODO should this have been set earlier...?
            node.getResult().setFailed(lastException);

            node.getResult().setScreenshot(lastFailure.getScreenshot());
        }
    }

    private void runTearDown(NODE_TYPE node, RootNodeExecutionContext context) {

        try {

            context.getSetupAndTeardown().runTearDown(getScope());
            ExecutionContext.clear(getScope());

        } catch (final Throwable t) {
            log.debug("tear down failed", t);

            context.addFailure(new SubstepExecutionFailure(t, node, true));
        }
    }

    protected boolean addExpectedChildrenFailureIfNoChildren(NODE_TYPE node, List<? extends IExecutionNode> children,
            RootNodeExecutionContext context) {

        boolean hasChildren = children != null && !children.isEmpty();
        if (!hasChildren) {
            context.addFailure(new SubstepExecutionFailure(new IllegalStateException(
                    "node should have children but doesn't"), node));
        }

        return hasChildren;
    }

}
