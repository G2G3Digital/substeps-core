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

import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.Scope;

public class BasicScenarioNodeRunner extends AbstractNodeRunner<BasicScenarioNode, Boolean> {

    private final SubstepNodeRunner backgroundRunner = new SubstepNodeRunner(Scope.SCENARIO_BACKGROUND);
    private final SubstepNodeRunner substepNodeRunner = new SubstepNodeRunner(Scope.STEP);
    private final StepImplementationNodeRunner stepImplNodeRunner = new StepImplementationNodeRunner();

    private RootNodeExecutionContext context;

    @Override
    protected boolean execute(BasicScenarioNode node, RootNodeExecutionContext context) {

        this.context = context;
        boolean success = runBackgroundIfPresent(node, context);

        Iterator<StepNode> stepIt = node.getSteps().iterator();

        while (success && stepIt.hasNext()) {

            StepNode step = stepIt.next();
            success &= step.dispatch(this);
        }

        return success;
    }

    public boolean runBackgroundIfPresent(BasicScenarioNode node, RootNodeExecutionContext context) {

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
