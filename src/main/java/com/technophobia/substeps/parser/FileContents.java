package com.technophobia.substeps.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;

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
/**
 * Encapsulate some details about a file; a list of lines, offsets of those
 * lines, plus the original content. Shared functionality between the feature
 * file parser and substeps parser
 */
public class FileContents {

    private List<String> lines = null;
    private int[] lineStartOffsets = null;
    private String fullContents = null;
    private File file;

    public List<String> getLines() {
        return this.lines;
    }

    /**
     * @return
     */
    public int getNumberOfLines() {
        return this.lines.size();
    }

    public File getFile() {
        return this.file;
    }

    public void readFile(final File file) throws IOException {

        this.file = file;
        this.lines = Files.readLines(file, Charset.forName("UTF-8"));

        this.lineStartOffsets = new int[this.lines.size()];

        this.fullContents = Files.toString(file, Charset.forName("UTF-8"));

        int lastOffset = 0;
        for (int i = 0; i < this.lines.size(); i++) {

            final String s = this.lines.get(i);

            this.lineStartOffsets[i] = this.fullContents.indexOf(s, lastOffset);
            lastOffset = this.lineStartOffsets[i] + s.length();
        }
    }

    public int getSourceLineNumber(final String line, final int offset) {

        int lineNumber = -1;
        // find the line from the offset
        final int idx = this.fullContents.indexOf(line);

        if (idx != -1) {
            // what's the line number of this offset ?
            lineNumber = getSourceLineNumberForOffset(offset);
        }
        return lineNumber;
    }

    public int getSourceLineNumberForOffset(final int offset) {

        int lineNumber = -1;
        lineNumber = 0;
        for (; lineNumber < this.lineStartOffsets.length; lineNumber++) {

            if (this.lineStartOffsets[lineNumber] > offset) {
                break;
            }
        }
        return lineNumber;
    }

    public int getEndOfLineOffset(final int lineNumber) {

        int lastOffset;
        if (lineNumber + 1 < this.lineStartOffsets.length) {
            lastOffset = this.lineStartOffsets[lineNumber + 1] - 1;
        } else {
            lastOffset = this.fullContents.length();
        }
        return lastOffset;
    }

    /**
     * @param lineNumberIdx
     * @return
     */
    public String getLineAt(final int lineNumberIdx) {

        return this.lines.get(lineNumberIdx);
    }

    /**
     * @param lineNumberIdx
     * @return
     */
    public int getSourceStartOffsetForLineIndex(final int lineNumberIdx) {

        return this.lineStartOffsets[lineNumberIdx];
    }

    /**
     * @return
     */
    public String getFullContent() {

        return this.fullContents;
    }

}
