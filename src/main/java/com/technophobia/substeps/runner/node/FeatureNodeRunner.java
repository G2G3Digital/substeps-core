package com.technophobia.substeps.runner.node;

import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.model.Scope;


public class FeatureNodeRunner extends AbstractNodeRunner<FeatureNode, Boolean> {

    BasicScenarioNodeRunner basicScenarioNodeRunner = new BasicScenarioNodeRunner();
    OutlineScenarioNodeRunner outlineScenarioNodeRunner = new OutlineScenarioNodeRunner();
    
    private NodeExecutionContext context;
    
    @Override
    protected boolean execute(FeatureNode node, NodeExecutionContext context) {

        this.context = context;
        
        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getChildren(), context);
        
        for (ScenarioNode<?> scenario : node.getChildren()) {
            
            success &= scenario.dispatch(this);
        }

        return success;
    }

    @Override
    protected Scope getScope() {

        return Scope.FEATURE;
    }

    @Override
    public Boolean visit(OutlineScenarioNode outlineScenarioNode) {
        
        return outlineScenarioNodeRunner.run(outlineScenarioNode, context);
    }
    
    @Override
    public Boolean visit(BasicScenarioNode basicScenarioNode) {
        
        return basicScenarioNodeRunner.run(basicScenarioNode, context);
    }
    
}
