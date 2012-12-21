package com.technophobia.substeps.runner.node;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.runner.ProvidesScreenshot;
import com.technophobia.substeps.runner.SubstepExecutionFailure;

public class StepImplementationNodeRunner extends AbstractNodeRunner<StepImplementationNode, Void> {

    @Override
    protected boolean execute(StepImplementationNode node, RootNodeExecutionContext context) {

        boolean success = false;

        try {

            context.getMethodExecutor().executeMethod(node.getTargetClass(), node.getTargetMethod(),
                    node.getMethodArgs());
            context.setTestsHaveRun();
            success = true;

        } catch (final InvocationTargetException e) {

            addFailure(node, context, e.getTargetException());

        } catch (final Throwable t) {

            addFailure(node, context, t);
        }

        return success;
    }

    private void addFailure(StepImplementationNode node, RootNodeExecutionContext context, Throwable t) {

        byte[] screenshotBytes = attemptScreenshot(node, context);
        context.addFailure(new SubstepExecutionFailure(t, node, screenshotBytes));
    }

    @Override
    protected Scope getScope() {

        return Scope.STEP;
    }

    @SuppressWarnings("unchecked")
    private <T> byte[] attemptScreenshot(StepImplementationNode node, RootNodeExecutionContext context) {

        return ProvidesScreenshot.class.isAssignableFrom(node.getTargetClass()) ? getScreenshot(context,
                (Class<? extends ProvidesScreenshot>) node.getTargetClass()) : null;
    }

    private <T extends ProvidesScreenshot> byte[] getScreenshot(RootNodeExecutionContext context, Class<T> screenshotClass) {

        T screenshotTakingInstance = context.getMethodExecutor().getImplementation(screenshotClass);
        return screenshotTakingInstance.getScreenshotBytes();
    }

}
