package com.technophobia.substeps.runner.node;

import java.util.Iterator;

import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.Scope;


public class SubstepNodeRunner extends AbstractNodeRunner<SubstepNode, Boolean> {

    private final Scope scope;
    
    private StepImplementationNodeRunner stepImplementationNodeRunner = new StepImplementationNodeRunner();
    private RootNodeExecutionContext context;
    
    public SubstepNodeRunner(Scope scope) {
        
        this.scope = scope;
    }
    
    @Override
    protected boolean execute(SubstepNode node, RootNodeExecutionContext context) {

        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getChildren(), context);
        this.context = context;
        
        Iterator<StepNode> substepsIt = node.getChildren().iterator();
        
        while(success && substepsIt.hasNext()) {
            
            success = substepsIt.next().dispatch(this);
        }

        return success;
    }

    @Override
    protected Scope getScope() {
        return scope;
    }

    @Override
    public Boolean visit(StepImplementationNode stepImplementationNode) {

        return stepImplementationNodeRunner.run(stepImplementationNode, context);
    }
    
    @Override
    public Boolean visit(SubstepNode substepsNode) {

        return this.run(substepsNode, context);
    }
    
}
