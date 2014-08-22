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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.DryRunImplementationCache;
import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.execution.node.ExecutionNodeUsage;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.NodeWithChildren;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
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

    private static final String DRY_RUN_KEY = "dryRun";

    private static final Logger log = LoggerFactory.getLogger(ExecutionNodeRunner.class);

    private RootNode rootNode;

    private final INotificationDistributor notificationDistributor = new NotificationDistributor();

    private RootNodeExecutionContext nodeExecutionContext;

    private final MethodExecutor methodExecutor = new ImplementationCache();

    private final RootNodeRunner rootNodeRunner = new RootNodeRunner();

    private List<SubstepExecutionFailure> failures;

    public void addNotifier(final IExecutionListener notifier) {

        this.notificationDistributor.addListener(notifier);
    }

    public RootNode prepareExecutionConfig(final SubstepsExecutionConfig theConfig) {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(theConfig);
        config.initProperties();

        final SetupAndTearDown setupAndTearDown = new SetupAndTearDown(config.getInitialisationClasses(),
                this.methodExecutor);

        final String loggingConfigName = config.getDescription() != null ? config.getDescription() : "SubStepsMojo";

        setupAndTearDown.setLoggingConfigName(loggingConfigName);

        final TagManager tagmanager = new TagManager(config.getTags());

        final TagManager nonFatalTagmanager = config.getNonFatalTags() != null ? new TagManager(
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
        this.rootNode = nodeTreeBuilder.buildExecutionNodeTree(theConfig.getDescription());

        // add any listeners (including the step execution logger)

        final List<Class<? extends IExecutionListener>> executionListenerClasses = config.getExecutionListenerClasses();

        for (final Class<? extends IExecutionListener> listener : executionListenerClasses) {

            log.info("adding executionListener: " + listener.getClass());

            try {
                this.notificationDistributor.addListener(listener.newInstance());
            } catch (final Exception e) {
                // not the end of the world...
                log.warn("failed to instantiate ExecutionListener: " + listener.getClass(), e);
            }
        }

        processUncalledAndUnused(syntax);
        
        ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY,
                this.notificationDistributor);

        final String dryRunProperty = System.getProperty(DRY_RUN_KEY);
        final boolean dryRun = dryRunProperty != null && Boolean.parseBoolean(dryRunProperty);

        final MethodExecutor methodExecutorToUse = dryRun ? new DryRunImplementationCache() : this.methodExecutor;

        if (dryRun) {
            log.info("**** DRY RUN ONLY **");
        }

        this.nodeExecutionContext = new RootNodeExecutionContext(this.notificationDistributor,
                Lists.<SubstepExecutionFailure> newArrayList(), setupAndTearDown, nonFatalTagmanager,
                methodExecutorToUse);

        return this.rootNode;
    }

    /**
     * @param syntax 
     * 
     */
    private void processUncalledAndUnused(final Syntax syntax) {
        final List<StepImplementation> uncalledStepImplementations = syntax.getUncalledStepImplementations();
        if (!uncalledStepImplementations.isEmpty()){
            final StringBuilder buf = new StringBuilder();
            buf.append("** Uncalled Step implementations in scope, this is suspect if these implementations are in your projects domain:\n\n");
            for (final StepImplementation s : uncalledStepImplementations){
                buf.append(s.getMethod()).append("\n");
            }
            buf.append("\n");
            log.info(buf.toString());
        }
        
        buildCallHierarchy();
        
        checkForUncalledParentSteps(syntax);
        
    }

    /**
     * @param syntax
     */
    private void checkForUncalledParentSteps(final Syntax syntax) {
        
        final Set<ExecutionNodeUsage> calledExecutionNodes = callerHierarchy.keySet();
        
        final StringBuilder buf = new StringBuilder();
        
        for (final ParentStep p : syntax.getSortedRootSubSteps()){
            
            // is there an executionnodeusage that is going to match ?
            
            final Step parent = p.getParent();
            
            if (thereIsNotAStepThatMatchesThisPattern(parent.getPattern(), calledExecutionNodes)){
                buf.append("\t")
                    .append( parent.getLine())
                    .append( " @ ")
                    .append(parent.getSource().getName())
                    .append( ":")
                    .append(parent.getSourceLineNumber())
                    .append("\n");
            }
        }
        if (buf.length()> 0){
            log.warn("** Substep definitions not called in current substep execution scope...\n\n" + buf.toString());
        }
    }

    private boolean thereIsNotAStepThatMatchesThisPattern(final String stepPattern, final Set<ExecutionNodeUsage> calledExecutionNodes){
        boolean found = false;
        
        final Iterator<ExecutionNodeUsage> it = calledExecutionNodes.iterator();
        
        while (it.hasNext() && !found){
            final ExecutionNodeUsage u = it.next();
            
            if (stepPattern == null || u.getDescription() == null){
                
                System.out.println("barrf");
            }
            
            found = Pattern.matches(stepPattern, u.getDescription());
        }
        // NB. return true if no match found!
        return !found;
    }
    
    // map of nodes to each of the parents, where this node is used
    final Map<ExecutionNodeUsage, List<ExecutionNodeUsage>> callerHierarchy = new HashMap<ExecutionNodeUsage, List<ExecutionNodeUsage>>();
    
    // a combined list of children wherever this node is used
    // TODO - to populate ?
    //final Map<ExecutionNodeUsage, List<ExecutionNodeUsage>> calleeHierarchy = new HashMap<ExecutionNodeUsage, List<ExecutionNodeUsage>>();
    
    
    /**
     * @param rootNode2
     */
    private void buildCallHierarchy() {
                
        final ExecutionNodeUsage rootUsage = new ExecutionNodeUsage(this.rootNode);
        
        callerHierarchy.put(rootUsage, null); // nothing calls this
        
        for (final FeatureNode feature : this.rootNode.getChildren()){
            
            addToCallHierarchy(feature);
            
            for (final ScenarioNode scenario : feature.getChildren()){

                addToCallHierarchy(scenario);

                processChildrenForCallHierarchy(scenario.getChildren());
            }
        }
    }

    /**
     * 
     */
        
    private void processChildrenForCallHierarchy(final List children){
        for (final Object obj : children){
            
            final IExecutionNode node = (IExecutionNode)obj;
            
            addToCallHierarchy(node);
            
            log.trace("looking at node description: " + node.getDescription() + " line: " + node.getLine());
            
            if (NodeWithChildren.class.isAssignableFrom(node.getClass())){
                final NodeWithChildren nodeWithChildren = (NodeWithChildren)node;
                log.trace("proccessing children...");
                processChildrenForCallHierarchy(nodeWithChildren.getChildren());
            }
        }
    }

    /**
     * @param node
     */
    private void addToCallHierarchy(final IExecutionNode node) {

        final ExecutionNodeUsage usage = new ExecutionNodeUsage(node);
        
        log.trace("building usage for desc: " + node.getDescription() + " line: " + node.getLine());
        
        List<ExecutionNodeUsage> immediateParents = callerHierarchy.get(usage);
        if (immediateParents == null){
            log.trace("no uses already for node...");
            immediateParents = new ArrayList<ExecutionNodeUsage>();
            callerHierarchy.put(usage, immediateParents);
        }
        else {
            log.trace("got existing usages of node: ");
            for (final ExecutionNodeUsage u : immediateParents){
                log.trace("already found: " + u.toString());
            }
        }
        log.trace("adding used by descr: " + node.getParent().getDescription() + " line: " + node.getParent().getLine());
        
        immediateParents.add(new ExecutionNodeUsage(node.getParent()));

    }

    public RootNode run() {

        // TODO - why is this here twice?
        ExecutionContext.put(Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY,
                this.notificationDistributor);

        this.rootNodeRunner.run(this.rootNode, this.nodeExecutionContext);

        if (!this.nodeExecutionContext.haveTestsBeenRun()) {

            final Throwable t = new IllegalStateException("No tests executed");
            this.rootNode.getResult().setFailed(t);
            this.notificationDistributor.onNodeFailed(this.rootNode, t);

            this.nodeExecutionContext.addFailure(new SubstepExecutionFailure(t, this.rootNode));
        }

        this.failures = this.nodeExecutionContext.getFailures();

        return this.rootNode;
    }

    public List<SubstepExecutionFailure> getFailures() {

        return this.failures;
    }

}
