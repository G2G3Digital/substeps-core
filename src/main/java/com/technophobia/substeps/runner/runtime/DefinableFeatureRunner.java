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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

        init(clazz, Arrays.asList(toClasses(System.getProperty("substepsImplClasses"))),
                System.getProperty("substepsFeatureFile"), "", System.getProperty("substepsFile"),
                toClasses(System.getProperty("beforeAndAfterProcessors")));
    }


    private Class<?>[] toClasses(final String beforeAndAfterProcessors) {
        final Collection<Class<?>> classes = new ArrayList<Class<?>>();
        if (!beforeAndAfterProcessors.trim().isEmpty()) {
            final String[] split = beforeAndAfterProcessors.split(";");
            for (final String beforeAndAfterProcessor : split) {
                final Class<?> processorClass = toClass(beforeAndAfterProcessor);
                if (processorClass != null) {
                    classes.add(processorClass);
                }
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }


    private Class<?> toClass(final String beforeAndAfterProcessor) {
        try {
            return Class.forName(beforeAndAfterProcessor);
        } catch (final ClassNotFoundException e) {
            // Class doesn't exist - for now, leave it
            return null;
        }
    }
}
