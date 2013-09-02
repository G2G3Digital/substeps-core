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

import java.io.File;

//TODO: we should be able to identify these at parse time
public class UnimplementedStepException extends SubstepsRuntimeException {

    private static final long serialVersionUID = 6807673963152251297L;

    public UnimplementedStepException(final String pattern, final File source, final int lineNumber) {
        super("[" + pattern + "] in source file: " + source.getAbsolutePath() + " line " + lineNumber
                + " is not a recognised step or substep implementation");
    }

}
