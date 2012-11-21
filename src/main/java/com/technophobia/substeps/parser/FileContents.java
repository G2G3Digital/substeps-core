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

    private List<String> currentFeatureFileLines = null;
    private int[] currentFileOffsets = null;
    private String currentOriginalFileContents = null;
    private File file;


    public List<String> getLines() {
        return this.currentFeatureFileLines;
    }


    /**
     * @return
     */
    public int getNumberOfLines() {
        return this.currentFeatureFileLines.size();
    }


    public File getFile() {
        return this.file;
    }


    public void readFile(final File file) throws IOException {

        this.file = file;
        this.currentFeatureFileLines = Files.readLines(file,
                Charset.forName("UTF-8"));

        this.currentFileOffsets = new int[this.currentFeatureFileLines.size()];

        this.currentOriginalFileContents = Files.toString(file,
                Charset.forName("UTF-8"));

        int lastOffset = 0;
        for (int i = 0; i < this.currentFeatureFileLines.size(); i++) {

            final String s = this.currentFeatureFileLines.get(i);

            this.currentFileOffsets[i] = this.currentOriginalFileContents
                    .indexOf(s, lastOffset);
            lastOffset = this.currentFileOffsets[i] + s.length();
        }
    }


    public int getSourceLineNumberForOffset(final int offset) {

        int lineNumber = -1;
        lineNumber = 0;
        for (; lineNumber < this.currentFileOffsets.length; lineNumber++) {

            if (this.currentFileOffsets[lineNumber] > offset) {
                break;
            }
        }
        return lineNumber;
    }


    public int getEndOfLineOffset(final int lineNumber) {

        int lastOffset;
        if (lineNumber + 1 < this.currentFileOffsets.length) {
            lastOffset = this.currentFileOffsets[lineNumber + 1] - 1;
        } else {
            lastOffset = this.currentOriginalFileContents.length();
        }
        return lastOffset;
    }


    /**
     * @param lineNumberIdx
     * @return
     */
    public String getLineAt(final int lineNumberIdx) {

        return this.currentFeatureFileLines.get(lineNumberIdx);
    }

}
