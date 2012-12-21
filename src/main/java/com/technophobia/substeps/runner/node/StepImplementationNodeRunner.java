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
package com.technophobia.substeps.runner.node;

import java.lang.reflect.InvocationTargetException;

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

    private <T extends ProvidesScreenshot> byte[] getScreenshot(RootNodeExecutionContext context,
            Class<T> screenshotClass) {

        T screenshotTakingInstance = context.getMethodExecutor().getImplementation(screenshotClass);
        return screenshotTakingInstance.getScreenshotBytes();
    }

}
