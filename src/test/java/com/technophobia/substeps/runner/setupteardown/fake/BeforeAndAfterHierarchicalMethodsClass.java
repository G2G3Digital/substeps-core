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
package com.technophobia.substeps.runner.setupteardown.fake;

import com.technophobia.substeps.runner.JunitFeatureRunner.AfterAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryScenario;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryScenario;

public class BeforeAndAfterHierarchicalMethodsClass extends BeforeAndAfterHierarchicalMethodsParentClass {

    @BeforeAllFeatures
    public void beforeAllFeatures() {
        isBeforeAllFeaturesHierarchyExecuted.add("Child");
    }


    @BeforeEveryFeature
    public void beforeEveryFeatures() {
        isBeforeEveryFeatureHierarchyExecuted.add("Child");
    }


    @BeforeEveryScenario
    public void beforeEveryScenario() {
        isBeforeEveryScenarioHierarchyExecuted.add("Child");
    }


    @AfterEveryScenario
    public void afterEveryScenario() {
        isAfterEveryScenarioHierarchyExecuted.add("Child");
    }


    @AfterEveryFeature
    public void afterEveryFeatures() {
        isAfterEveryFeatureHierarchyExecuted.add("Child");
    }


    @AfterAllFeatures
    public void afterAllFeatures() {
        isAfterAllFeaturesHierarchyExecuted.add("Child");
    }
}
