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

import com.technophobia.substeps.model.StepImplementation;

public class DuplicateStepImplementationException extends StepImplementationException {

    private static final long serialVersionUID = 6851509341143564326L;

    public DuplicateStepImplementationException(final String pattern, final StepImplementation originalSource,
            final StepImplementation duplicatingSource) {

        super(duplicatingSource.getImplementedIn(), duplicatingSource.getMethod(), String.format(
                "Duplicate step implementation detected: Pattern [%s] is implemented in %s and %s", pattern,
                fullMethodNameFrom(originalSource), fullMethodNameFrom(duplicatingSource)));

    }

    private static String fullMethodNameFrom(final StepImplementation stepImplementation) {
        return stepImplementation.getImplementedIn().getName() + "." + stepImplementation.getMethod().getName();
    }

}
