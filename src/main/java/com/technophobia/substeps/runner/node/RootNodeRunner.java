package com.technophobia.substeps.runner.node;

import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.model.Scope;


public class RootNodeRunner extends AbstractNodeRunner<RootNode, Void> {

    FeatureNodeRunner featureNodeRunner = new FeatureNodeRunner();
    
    @Override
    protected boolean execute(RootNode node, NodeExecutionContext context) {

        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getChildren(), context);
        
        for(FeatureNode feature : node.getChildren()) {
            
            success &= featureNodeRunner.run(feature, context);
        }
        
        return success;
    }

    @Override
    protected Scope getScope() {
        
        return Scope.SUITE;
        
    }

}
