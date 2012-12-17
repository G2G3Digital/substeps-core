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

import com.technophobia.substeps.execution.node.NodeExecutionContext;
import com.technophobia.substeps.execution.node.OutlineScenarioNode;
import com.technophobia.substeps.execution.node.OutlineScenarioRowNode;
import com.technophobia.substeps.model.Scope;

public class OutlineScenarioNodeRunner extends AbstractNodeRunner<OutlineScenarioNode, Void> {

    private final OutlineScenarioRowNodeRunner outlineScenarioRowNodeRunner = new OutlineScenarioRowNodeRunner();

    @Override
    protected boolean execute(OutlineScenarioNode node, NodeExecutionContext context) {

        boolean success = addExpectedChildrenFailureIfNoChildren(node, node.getChildren(), context);

        for (OutlineScenarioRowNode outlineRow : node.getChildren()) {

            success &= outlineScenarioRowNodeRunner.run(outlineRow, context);
        }

        return success;
    }

    @Override
    protected Scope getScope() {

        return Scope.SCENARIO_OUTLINE;
    }

}
