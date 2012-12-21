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

import com.technophobia.substeps.execution.node.BasicScenarioNode;
import com.technophobia.substeps.execution.node.FeatureNode;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.RootNodeExecutionContext;
import com.technophobia.substeps.execution.node.ScenarioNode;
import com.technophobia.substeps.model.Scope;

public class FeatureNodeRunner extends AbstractNodeRunner<FeatureNode, Boolean> {

    BasicScenarioNodeRunner basicScenarioNodeRunner = new BasicScenarioNodeRunner();
    OutlineScenarioNodeRunner outlineScenarioNodeRunner = new OutlineScenarioNodeRunner();

    private RootNodeExecutionContext context;

    @Override
    protected boolean execute(FeatureNode node, RootNodeExecutionContext context) {

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
