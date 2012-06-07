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

import java.util.ArrayList;
import java.util.List;

import com.technophobia.substeps.runner.setupteardown.Annotations.AfterAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;



public class StaticAnnotatedMethodsHierarchicalFakeParent {

    public static List<String> isBeforeAllFeaturesHierarchyExecuted = new ArrayList<String>();
    public static List<String> isBeforeEveryFeatureHierarchyExecuted = new ArrayList<String>();
    public static List<String> isBeforeEveryScenarioHierarchyExecuted = new ArrayList<String>();

    public static List<String> isAfterEveryScenarioHierarchyExecuted = new ArrayList<String>();
    public static List<String> isAfterEveryFeatureHierarchyExecuted = new ArrayList<String>();
    public static List<String> isAfterAllFeaturesHierarchyExecuted = new ArrayList<String>();


    @BeforeAllFeatures
    public static void beforeAllFeaturesParent() {
        isBeforeAllFeaturesHierarchyExecuted.add("Parent");
    }


    @BeforeEveryFeature
    public static void beforeEveryFeaturesParent() {
        isBeforeEveryFeatureHierarchyExecuted.add("Parent");
    }


    @BeforeEveryScenario
    public static void beforeEveryScenarioParent() {
        isBeforeEveryScenarioHierarchyExecuted.add("Parent");
    }


    @AfterEveryScenario
    public static void afterEveryScenarioParent() {
        isAfterEveryScenarioHierarchyExecuted.add("Parent");
    }


    @AfterEveryFeature
    public static void afterEveryFeaturesParent() {
        isAfterEveryFeatureHierarchyExecuted.add("Parent");
    }


    @AfterAllFeatures
    public static void afterAllFeaturesParent() {
        isAfterAllFeaturesHierarchyExecuted.add("Parent");
    }
}
