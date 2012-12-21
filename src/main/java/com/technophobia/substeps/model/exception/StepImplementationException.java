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

import java.lang.reflect.Method;

public class StepImplementationException extends SubstepsException {

    private static final long serialVersionUID = 2361658683635343317L;

    private final Class<?> implementingClass;
    private final Method implementingMethod;

    public StepImplementationException(final Class<?> implementingClass, final Method implementingMethod,
            final String message) {
        super(message);
        this.implementingClass = implementingClass;
        this.implementingMethod = implementingMethod;
    }

    public Class<?> getImplementingClass() {
        return implementingClass;
    }

    public Method getImplementingMethod() {
        return implementingMethod;
    }

}
