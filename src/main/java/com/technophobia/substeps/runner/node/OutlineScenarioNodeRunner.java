package com.technophobia.substeps.runner.node;

import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.model.Scope;


public class OutlineScenarioNodeRunner extends AbstractNodeRunner<OutlineScenarioNode, Void> {

    private OutlineScenarioRowNodeRunner outlineScenarioRowNodeRunner = new OutlineScenarioRowNodeRunner();
    
    @Override
    protected boolean execute(OutlineScenarioNode node, NodeExecutionContext context) {

        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getOutlineRows(), context);
        
        for(OutlineScenarioRowNode outlineRow : node.getOutlineRows()) {
            
            success &= outlineScenarioRowNodeRunner.run(outlineRow, context);
        }
        
        return success;
    }

    @Override
    protected Scope getScope() {

        return null; //TODO Add new scope for which there is no setup and tear down Scope.OUTLINE;
    }

    
}
