package com.technophobia.substeps.runner.node;

import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.model.Scope;


public class BasicScenarioNodeRunner extends AbstractNodeRunner<BasicScenarioNode, Void> {

    private SubstepNodeRunner backgroundRunner = new SubstepNodeRunner(Scope.SCENARIO_BACKGROUND);
    private SubstepNodeRunner stepRunner = new SubstepNodeRunner(Scope.STEP);
    
    @Override
    protected boolean execute(BasicScenarioNode node, NodeExecutionContext context) {

        return runBackgroundIfPresent(node, context) && stepRunner.run(node.getStep(), context);
    }
    
    public boolean runBackgroundIfPresent(BasicScenarioNode node, NodeExecutionContext context) {
        
        return node.getBackground() == null || backgroundRunner.run(node.getBackground(), context);
        
    }

    @Override
    protected Scope getScope() {

        return Scope.SCENARIO;
    }
}
