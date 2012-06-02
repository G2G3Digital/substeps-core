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

import com.technophobia.substeps.runner.setupteardown.Annotations.AfterAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;


public class StaticAnnotatedMethodsFakeObject {

    public static boolean isBeforeAllFeaturesExecuted = false;
    public static boolean isBeforeEveryFeatureExecuted = false;
    public static boolean isBeforeEveryScenarioExecuted = false;

    public static boolean isAfterEveryScenarioExecuted = false;
    public static boolean isAfterEveryFeatureExecuted = false;
    public static boolean isAfterAllFeaturesExecuted = false;


    @BeforeAllFeatures
    public static void beforeAllFeatures() {
        isBeforeAllFeaturesExecuted = true;
    }


    @BeforeEveryFeature
    public static void beforeEveryFeatures() {
        isBeforeEveryFeatureExecuted = true;
    }


    @BeforeEveryScenario
    public static void beforeEveryScenario() {
        isBeforeEveryScenarioExecuted = true;
    }


    @AfterEveryScenario
    public static void afterEveryScenario() {
        isAfterEveryScenarioExecuted = true;
    }


    @AfterEveryFeature
    public static void afterEveryFeatures() {
        isAfterEveryFeatureExecuted = true;
    }


    @AfterAllFeatures
    public static void afterAllFeatures() {
        isAfterAllFeaturesExecuted = true;
    }
}
