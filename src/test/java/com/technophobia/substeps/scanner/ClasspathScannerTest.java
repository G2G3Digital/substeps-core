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
package com.technophobia.substeps.scanner;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.SubSteps;
import com.technophobia.substeps.stepimplementations.BDDRunnerStepImplementations;

/**
 * 
 * @author imoore
 * 
 */
public class ClasspathScannerTest {

    @Test
    public void testClasspathResolution() {
        // can get the classpath like this:
        System.out.println(System.getProperty("java.class.path"));

        final ClasspathScanner cpScanner = new ClasspathScanner();

        final List<Class<?>> classesWithAnnotation = cpScanner.getClassesWithAnnotation(
                SubSteps.StepImplementations.class, Thread.currentThread().getContextClassLoader());

        Assert.assertNotNull(classesWithAnnotation);

        Assert.assertThat(classesWithAnnotation.contains(BDDRunnerStepImplementations.class),
                is(true));

        // if elem is a jar, open up and take a look - lengthy?

        // if the path element is a folder - assume its full of java classes,
        // construct a name and load up via the classloader

        // how to actually get the classes from
    }
}
