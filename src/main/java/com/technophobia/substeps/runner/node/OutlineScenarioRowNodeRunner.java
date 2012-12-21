package com.technophobia.substeps.runner.node;

import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.model.Scope;


public class OutlineScenarioRowNodeRunner extends AbstractNodeRunner<OutlineScenarioRowNode, Void> {

    BasicScenarioNodeRunner basicScenarioNodeRunner = new BasicScenarioNodeRunner();
    
    @Override
    protected boolean execute(OutlineScenarioRowNode node, RootNodeExecutionContext context) {

        return basicScenarioNodeRunner.run(node.getBasicScenarioNode(), context);
        
    }
    @Override
    protected Scope getScope() {

        return Scope.SCENARIO_OUTLINE_ROW;
    }

    
}
