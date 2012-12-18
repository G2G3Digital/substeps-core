package com.technophobia.substeps.runner.node;

import java.util.Iterator;

import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.Scope;

public class BasicScenarioNodeRunner extends AbstractNodeRunner<BasicScenarioNode, Boolean> {

    private final SubstepNodeRunner backgroundRunner = new SubstepNodeRunner(Scope.SCENARIO_BACKGROUND);
    private final SubstepNodeRunner substepNodeRunner = new SubstepNodeRunner(Scope.STEP);
    private final StepImplementationNodeRunner stepImplNodeRunner = new StepImplementationNodeRunner();

    private NodeExecutionContext context;

    @Override
    protected boolean execute(BasicScenarioNode node, NodeExecutionContext context) {

        this.context = context;
        boolean success = runBackgroundIfPresent(node, context);

        Iterator<StepNode> stepIt = node.getSteps().iterator();

        while (success && stepIt.hasNext()) {

            StepNode step = stepIt.next();
            success &= step.dispatch(this);
        }

        return success;
    }

    public boolean runBackgroundIfPresent(BasicScenarioNode node, NodeExecutionContext context) {

        return node.getBackground() == null || backgroundRunner.run(node.getBackground(), context);

    }

    @Override
    public Boolean visit(StepImplementationNode stepImplementationNode) {

        return stepImplNodeRunner.run(stepImplementationNode, context);
    }

    @Override
    public Boolean visit(SubstepNode substepsNode) {

        return substepNodeRunner.run(substepsNode, context);
    }

    @Override
    protected Scope getScope() {

        return Scope.SCENARIO;
    }
}
