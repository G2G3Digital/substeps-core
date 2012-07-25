/*
 *  Copyright Technophobia Ltd 2012
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.technophobia.substeps.runner.runtime.fake.AnnotatedStepClassFake;
import com.technophobia.substeps.runner.runtime.fake.NonAnnotatedStepClassFake;

public class PredicatedClassLocatorTest {

    private ClassLocator classLocator;

    private Predicate<Class<?>> predicate;
    private Function<File, Class<?>> classLoaderFunc;


    @SuppressWarnings("unchecked")
    @Before
    public void initialise() {
        predicate = mock(Predicate.class);
        classLoaderFunc = mock(Function.class);

        classLocator = new PredicatedClassLocator(predicate, classLoaderFunc);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void onlyFilesMatchingPredicateAreLocated() {
        when(predicate.apply(AnnotatedStepClassFake.class)).thenReturn(true);
        when(predicate.apply(NonAnnotatedStepClassFake.class)).thenReturn(false);

        final Class returnClass = AnnotatedStepClassFake.class;
        when(
                classLoaderFunc.apply(eq(new File(asFile("fake/"
                        + AnnotatedStepClassFake.class.getSimpleName() + ".class"))))).thenReturn(
                returnClass);
        final Iterator<Class<?>> classes = classLocator.fromPath(asFile("fake"));
        assertTrue(classes.hasNext());
        assertEquals(AnnotatedStepClassFake.class, classes.next());
        assertFalse(classes.hasNext());
    }


    private String asFile(final String relPath) {
        return getClass().getResource(relPath).getFile();
    }
}
