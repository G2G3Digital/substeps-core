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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.setupteardown.SetupAndTearDown;
import com.technophobia.substeps.runner.syntax.SyntaxBuilder;

/**
 * Takes a tree of execution nodes and executes them, all variables, args,
 * backgrounds already pre-determined
 * 
 * @author ian
 * 
 */
public class ExecutionNodeRunner {
    private final Logger log = LoggerFactory.getLogger(ExecutionNodeRunner.class);

    private boolean noTestsRun = true;

    private boolean dryRun;

    private ExecutionNode rootNode;
    private INotifier notifier;
    private SetupAndTearDown setupAndTearDown;
    private ExecutionConfig config;

    private final MethodExecutor methodExecutor = new ImplementationCache();


    public ExecutionNode prepareExecutionConfig(final ExecutionConfig theConfig,
            final INotifier notifierParam) {

        notifier = notifierParam;
        config = theConfig;
        config.initProperties();

        setupAndTearDown = new SetupAndTearDown(config.getInitialisationClasses(), methodExecutor);

        final String loggingConfigName = config.getDescription() != null ? config.getDescription()
                : "SubStepsMojo";

        setupAndTearDown.setLoggingConfigName(loggingConfigName);

        final TagManager tagmanager = new TagManager(config.getTags());

        File subStepsFile = null;

        if (config.getSubStepsFileName() != null) {
            subStepsFile = new File(config.getSubStepsFileName());
        }

        final Syntax syntax = SyntaxBuilder.buildSyntax(config.getStepImplementationClasses(),
                subStepsFile, config.isStrict(), config.getNonStrictKeywordPrecedence());

        final TestParameters parameters = new TestParameters(tagmanager, syntax,
                config.getFeatureFile());

        parameters.setFailParseErrorsImmediately(config.isFastFailParseErrors());
        parameters.init();

        final ExecutionNodeTreeBuilder nodeTreeBuilder = new ExecutionNodeTreeBuilder(parameters);

        // building the tree can throw critical failures if exceptions are found
        rootNode = nodeTreeBuilder.buildExecutionNodeTree();

        ExecutionContext.put(Scope.SUITE, JunitNotifier.NOTIFIER_EXECUTION_KEY, notifier);

        final String dryRunProperty = System.getProperty("dryRun");
        if (dryRunProperty != null && Boolean.parseBoolean(dryRunProperty)) {

            log.info("**** DRY RUN ONLY **");

            setupAndTearDown.setDryRun(true);
            setDryRun(true);
        }

        return rootNode;
    }


    public void run() {
        log.debug("run root node");
        noTestsRun = true;

        ExecutionContext.put(Scope.SUITE, JunitNotifier.NOTIFIER_EXECUTION_KEY, notifier);

        try {
            runExecutionNodeHierarchy(Scope.SUITE, rootNode);
        } catch (final Throwable e) {
            log.error("root node exception", e);

        }

        if (noTestsRun) {

            final Throwable t = new IllegalStateException("No tests executed");
            rootNode.getResult().setFailed(t);
            notifier.notifyTestFailed(rootNode, t);

        }

    }


    private boolean runExecutionNodeHierarchy(final Scope scope, final ExecutionNode node)
            throws Throwable {

        log.info("run Node Hierarchy @ " + scope.name() + ":" + node.getDebugStringForThisNode());

        // do notify for this node

        boolean success = true;
        Throwable theException = null;

        notifier.notifyTestStarted(node);

        // node may have parsing error, in which case, bail immediately
        if (node.hasError()) {
            //
            success = false;
            theException = node.getResult().getThrown();
            notifier.notifyTestFailed(node, theException);
            throw theException;
        }

        if (success) {
            node.getResult().setStarted();

            // run setup if necessary for this depth and step
            // if this fails then we bail & mark as failed
            try {
                if (!node.isOutlineScenario()) {
                    setupAndTearDown.runSetup(scope);
                }
            } catch (final Throwable t) {
                log.debug("setup failed", t);

                success = false;
                theException = t;
            }
        }

        // if background, run backgrounds
        if (success && node.hasBackground() && !node.isOutlineScenario()) {
            // any of these fail then bail & mark this node as failed
            for (final ExecutionNode backgroundNode : node.getBackgrounds()) {
                try {

                    final boolean thisExecutionSuccess = runExecutionNodeHierarchy(
                            Scope.SCENARIO_BACKGROUND, backgroundNode);
                    if (success && !thisExecutionSuccess) {
                        success = thisExecutionSuccess;
                    }
                } catch (final Throwable t) {
                    log.debug("scenario background failed", t);

                    success = false;
                    theException = t;
                }
                if (!success) {
                    break;
                }
            }
        }

        if (success && node.isExecutable()) {
            log.debug("executing node method");

            // if executable invoke
            try {

                if (!dryRun) {
                    methodExecutor.executeMethod(node.getTargetClass(), node.getTargetMethod(),
                            node.getMethodArgs());
                }
                noTestsRun = false;

            } catch (final InvocationTargetException e) {

                theException = e.getTargetException();
                success = false;

            } catch (final Throwable e) {

                theException = e;
                success = false;
            }
        }

        // if children, run children

        else if (success && node.shouldHaveChildren() && !node.hasChildren()) {
            success = false;
            // TODO - better error message required
            theException = new IllegalStateException("node should have children but doesn't");
        }

        else if (success && node.hasChildren()) {
            log.debug("node has children");
            // if any fail, mark this as failed. if current scope is
            // suite, feature then continue even if failure
            // if scenario or outline or step, bail
            for (final ExecutionNode child : node.getChildren()) {
                final Scope childScope = getChildScope(node, scope);

                try {

                    final boolean thisExecutionSuccess = runExecutionNodeHierarchy(childScope,
                            child);

                    if (success && !thisExecutionSuccess) {
                        success = thisExecutionSuccess;
                    }
                } catch (final Throwable t) {
                    log.debug("running children failed", t);

                    success = false;
                    theException = t;
                }

                // bail out if current scope is Step or SCENARIO_OUTLINE_ROW

                // bail if current scope is scenario and childscope is step
                if (!success
                        && (scope == Scope.STEP || scope == Scope.SCENARIO_OUTLINE_ROW || (scope == Scope.SCENARIO && childScope == Scope.STEP))) {
                    log.debug("bailing out of execution");
                    break;
                }
            }
        }

        try {
            // run tear down if necessary for this depth and step
            if (!node.isOutlineScenario()) {
                setupAndTearDown.runTearDown(scope);
                ExecutionContext.clear(scope);

            }
        } catch (final Throwable t) {
            log.debug("tear down failed", t);

            success = false;
            theException = t;

        }

        if (success) {
            log.debug("node success");
            notifier.notifyTestFinished(node);

            node.getResult().setFinished();

        } else {
            if (theException == null) {
                theException = new IllegalStateException("failure with no exception");
            }

            log.debug("node failure", theException);

            notifier.notifyTestFailed(node, theException);

            node.getResult().setFailed(theException);

            throw theException;
        }
        // do notify finished

        return success;
    }


    private Scope getChildScope(final ExecutionNode node, final Scope currentScope) {
        Scope rtn = null;

        // maybe a neater way of doing this rather than a case statement...
        switch (currentScope) {
        case SUITE: {
            rtn = Scope.FEATURE;
            break;
        }
        case FEATURE: {
            rtn = Scope.SCENARIO;

            break;
        }
        case SCENARIO: {
            // a scenario can go into outline row or steps
            if (node.isOutlineScenario()) {
                rtn = Scope.SCENARIO_OUTLINE_ROW;
            } else {
                rtn = Scope.STEP;
            }
            break;
        }
        case SCENARIO_BACKGROUND:
        case SCENARIO_OUTLINE_ROW:
        case STEP: {
            rtn = Scope.STEP;
            break;
        }
        default: {
            throw new IllegalStateException("impossible state");
        }

        }

        log.debug("child scope: " + rtn.name() + " for: " + currentScope.name());

        return rtn;
    }


    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }


    public void setNotifier(final INotifier notifier) {
        this.notifier = notifier;
    }
}
