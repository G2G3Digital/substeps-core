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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.builder.ExecutionNodeTreeBuilder;
import com.technophobia.substeps.runner.node.RootNodeRunner;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;
import com.technophobia.substeps.runner.syntax.SyntaxBuilder;

/**
 * Takes a tree of execution nodes and executes them, all variables, args,
 * backgrounds already pre-determined
 * 
 * @author ian
 * 
 */
public class ExecutionNodeRunner implements SubstepsRunner {

    private RootNode rootNode;

    private final INotificationDistributor notificationDistributor = new NotificationDistributor();

    private NodeExecutionContext nodeExecutionContext;

    private final MethodExecutor methodExecutor = new ImplementationCache();
    
    private final RootNodeRunner rootNodeRunner = new RootNodeRunner();

    public void addNotifier(final INotifier notifier) {

        notificationDistributor.addListener(notifier);
    }

    public void prepareExecutionConfig(final SubstepsExecutionConfig theConfig) {

        ExecutionConfigWrapper config = new ExecutionConfigWrapper(theConfig);
        config.initProperties();

        SetupAndTearDown setupAndTearDown = new SetupAndTearDown(config.getInitialisationClasses(), methodExecutor);

        final String loggingConfigName = config.getDescription() != null ? config.getDescription() : "SubStepsMojo";

        setupAndTearDown.setLoggingConfigName(loggingConfigName);

        final TagManager tagmanager = new TagManager(config.getTags());

        final TagManager nonFatalTagmanager = (config.getNonFatalTags() != null) ? new TagManager(
                config.getNonFatalTags()) : null;

        File subStepsFile = null;

        if (config.getSubStepsFileName() != null) {
            subStepsFile = new File(config.getSubStepsFileName());
        }

        final Syntax syntax = SyntaxBuilder.buildSyntax(config.getStepImplementationClasses(), subStepsFile,
                config.isStrict(), config.getNonStrictKeywordPrecedence());

        final TestParameters parameters = new TestParameters(tagmanager, syntax, config.getFeatureFile());

        parameters.setFailParseErrorsImmediately(config.isFastFailParseErrors());
        parameters.init();

        final ExecutionNodeTreeBuilder nodeTreeBuilder = new ExecutionNodeTreeBuilder(parameters);

        // building the tree can throw critical failures if exceptions are found
        rootNode = nodeTreeBuilder.buildExecutionNodeTree();

        ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY, notificationDistributor);

        // TODO RB Put dry run back in
        // final String dryRunProperty = System.getProperty("dryRun");
        // if (dryRunProperty != null && Boolean.parseBoolean(dryRunProperty)) {
        //
        // log.info("**** DRY RUN ONLY **");
        //
        // setupAndTearDown.setDryRun(true);
        // setDryRun(true);
        // }

        nodeExecutionContext = new NodeExecutionContext(notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, nonFatalTagmanager);
    }

    public List<SubstepExecutionFailure> run() {

        // TODO - why is this here twice?
        ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY, notificationDistributor);

        rootNodeRunner.run(rootNode, nodeExecutionContext);

        if (!nodeExecutionContext.haveTestsBeenRun()) {

            final Throwable t = new IllegalStateException("No tests executed");
            rootNode.getResult().setFailed(t);
            notificationDistributor.notifyNodeFailed(rootNode, t);

            nodeExecutionContext.addFailure(new SubstepExecutionFailure(t, rootNode));
        }

        return nodeExecutionContext.getFailures();
    }

    public RootNode getRootNode() {

        return this.rootNode;
    }

}
