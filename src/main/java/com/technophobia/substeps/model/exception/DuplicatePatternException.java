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

import com.technophobia.substeps.model.ParentStep;

public class DuplicatePatternException extends SubstepsParsingException {

    private static final long serialVersionUID = 2426674698756596968L;


    public DuplicatePatternException(final String pattern, final ParentStep originalSource,
            final ParentStep duplicatingSource) {
        // TODO - offset
        super(new File(duplicatingSource.getSubStepFileUri()), originalSource.getParent().getSourceLineNumber(),
                originalSource.getParent().getLine(), duplicatingSource.getParent().getSourceStartOffset(),
                messageFrom(pattern, originalSource, duplicatingSource));

    }


    private static String messageFrom(final String pattern, final ParentStep originalSource,
            final ParentStep duplicatingSource) {
        // TODO: is 'pattern' actually a end-user friendly word?
        return String.format("Duplicate pattern detected: Pattern [%s] is defined in %s and %s", pattern,
                patternLocationFrom(originalSource), patternLocationFrom(duplicatingSource));
    }


    private static String patternLocationFrom(final ParentStep parentStep) {
        return parentStep.getSubStepFile() + "::" + parentStep.getParent().getSourceLineNumber();
    }
}
