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
package com.technophobia.substeps.model.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.technophobia.substeps.model.StepImplementation;

public class DuplicateStepImplementationExceptionTest {

    @Test
    public void shouldProduceSuitableMessage() throws NoSuchMethodException, SecurityException {
        final DuplicateStepImplementationException ex = new DuplicateStepImplementationException("test-pattern",
                stepImplementation("originalMethod"), stepImplementation("duplicatingMethod"));

        assertThat(ex.getMessage(),
                is("Duplicate step implementation detected: Pattern [test-pattern] is implemented in "
                        + getClass().getName() + ".originalMethod and " + getClass().getName() + ".duplicatingMethod"));
    }

    private StepImplementation stepImplementation(final String methodName) throws NoSuchMethodException,
            SecurityException {
        return new StepImplementation(getClass(), "keyword", "test-pattern", getClass().getMethod(methodName));
    }

    public void originalMethod() {
        // NoOp - here for testing.
    }

    public void duplicatingMethod() {
        // NoOp - here for testing.
    }
}
