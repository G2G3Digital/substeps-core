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
package com.technophobia.substeps.runner.setupteardown;

import java.lang.annotation.Annotation;

import com.technophobia.substeps.runner.setupteardown.Annotations.AfterAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;

public enum MethodState {
    BEFORE_ALL(true, BeforeAllFeatures.class), //
    BEFORE_FEATURES(true, BeforeEveryFeature.class), //
    BEFORE_SCENARIOS(true, BeforeEveryScenario.class), //
    AFTER_SCENARIOS(false, AfterEveryScenario.class), //
    AFTER_FEATURES(false, AfterEveryFeature.class), //
    AFTER_ALL(false, AfterAllFeatures.class); //

    private final boolean beforeTest;
    private final Class<? extends Annotation> annotationClass;


    private MethodState(final boolean beforeTest, final Class<? extends Annotation> annotationClass) {
        this.beforeTest = beforeTest;
        this.annotationClass = annotationClass;
    }


    public boolean isBeforeTest() {
        return beforeTest;
    }


    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
