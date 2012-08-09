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
package com.technophobia.substeps.runner.runtime;

import java.io.File;

import com.google.common.collect.Lists;
import com.technophobia.substeps.runner.JunitFeatureRunner;

public class DefinableFeatureRunner extends JunitFeatureRunner {

    public DefinableFeatureRunner() {
        super();
    }


    // public DefinableFeatureRunner(final IJunitNotifier notifier) {
    // super(notifier);
    // }

    public DefinableFeatureRunner(final Class<?> clazz) {
        super();
        final String outputFolder = System.getProperty("outputFolder");
        final String path = new File(outputFolder).getAbsolutePath();
        final ClassLocator classLocator = new StepClassLocator(path);

        init(clazz, Lists.newArrayList(classLocator.fromPath(path)),
                System.getProperty("substepsFeatureFile"), "", "", new Class<?>[0]);
    }
}
