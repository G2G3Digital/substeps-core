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

public class SubstepsParsingException extends SubstepsException {

    private static final long serialVersionUID = 3310663144363390571L;

    private final File file;
    private final long offset;
    private final String line;
    private final int lineNumber;

    public SubstepsParsingException(final File file, final int lineNumber, final String line, final long offset,
            final String message) {
        super(message);
        this.file = file;
        this.line = line;
        this.offset = offset;
        this.lineNumber = lineNumber;
    }

    public File getFile() {
        return file;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public long getOffset() {
        return offset;
    }

}
