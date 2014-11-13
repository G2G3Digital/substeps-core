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
package com.technophobia.substeps.runner.node;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.Scope;

public class SubstepNodeRunner extends AbstractNodeRunner<SubstepNode, Boolean> {

    private static final Logger log = LoggerFactory.getLogger(SubstepNodeRunner.class);

    private final Scope scope;

    private final StepImplementationNodeRunner stepImplementationNodeRunner = new StepImplementationNodeRunner();
    private RootNodeExecutionContext context;

    public SubstepNodeRunner(Scope scope) {

        this.scope = scope;
    }

    @Override
    protected boolean execute(SubstepNode node, RootNodeExecutionContext context) {
        log.debug("Executing substep {}", node.getDescription());

        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getChildren(), context);
        this.context = context;

        Iterator<StepNode> substepsIt = node.getChildren().iterator();

        while (success && substepsIt.hasNext()) {

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
